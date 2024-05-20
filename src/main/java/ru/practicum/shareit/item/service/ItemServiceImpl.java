package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validator.BookingDtoInfo;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private static final String NEXT = "next";
    private static final String LAST = "last";

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.getUserDtoById(userId));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));
        return itemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoInfo findItemById(Long userId, Long itemId) {
        Item item = getItemById(itemId, userId);
        List<Comment> comments = commentRepository.findByItemId(itemId).orElse(new ArrayList<>());
        Map<Long, List<CommentDto>> commentItems = getCommentDtoSortByIdItem(comments);
        boolean isOwner = itemRepository.existsByIdAndOwner_Id(itemId, userId);
        if (!isOwner) {
            List<CommentDto> commentDto = commentItems.isEmpty() ? new ArrayList<>() : commentItems.get(item.getId());
            return itemMapper.toOneItemDtoInfoForAllUsers(item, commentDto);
        }
        return setBookingForOwner(List.of(item), List.of(itemId), commentItems).stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    @Override
    public Collection<ItemDtoInfo> getAllItemUser(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemsId).orElse(new ArrayList<>());
        Map<Long, List<CommentDto>> commentsItems = getCommentDtoSortByIdItem(comments);
        return setBookingForOwner(items, itemsId, commentsItems);
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItemByIdAvailable(Long itemId, Long userId) {
        Item item = getItemById(itemId, userId);
        if (item.getAvailable().equals(false)) {
            throw new ValidationException("Предмет с данным id={}" + itemId + " не найден или не доступен");
        }
        return item;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> itemList = itemRepository.searchItems(text);
        return itemMapper.toItemDtoCollection(itemList);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.getUserDtoById(userId));
        Item item = itemRepository.findById(itemId).stream().findFirst().orElse(null);
        if (item == null || !item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Предмет с данным id={}" + itemId + " не найден");
        }
        setItemDto(item, itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException("Предмет еще не существует"));
        isBookerOfThisItem(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, user, item);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private void setItemDto(Item itemOld, ItemDto itemDto, User owner) {
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            itemOld.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            itemOld.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemOld.setAvailable(itemDto.getAvailable());
        }
        itemOld.setOwner(owner);
    }

    private Item getItemById(Long itemId, Long userId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Предмет с данным id={} не найден для пользователя с id={}", itemId, userId);
            return new NotFoundException("Предмет с данным id={}" + itemId + " не найден");
        });
    }

    private Map<String, Map<Long, BookingDtoInfo>> getBookingDtoInfoMapByNextAndLast(List<Booking> nextBooking, List<Booking> lastBooking) {
        List<BookingDtoInfo> nextBookingDtoInfo = bookingMapper.toBookingDtoInfoList(nextBooking);
        List<BookingDtoInfo> lastBookingDtoInfo = bookingMapper.toBookingDtoInfoList(lastBooking);

        Map<Long, BookingDtoInfo> next = bookingMapper.toBookingDtoInfoMapByIdItem(nextBookingDtoInfo);
        Map<Long, BookingDtoInfo> last = bookingMapper.toBookingDtoInfoMapByIdItem(lastBookingDtoInfo);
        Map<String, Map<Long, BookingDtoInfo>> result = new HashMap<>();
        if (!next.isEmpty()) {
            result.put(NEXT, next);
        }
        if (!last.isEmpty()) {
            result.put(LAST, last);
        }
        return result;
    }

    private Collection<ItemDtoInfo> getItemDtoInfoForOwner(List<Item> items, List<Booking> next, List<Booking> last, Map<Long, List<CommentDto>> commentsItem) {
        Map<String, Map<Long, BookingDtoInfo>> booking = getBookingDtoInfoMapByNextAndLast(next, last);
        Map<Long, BookingDtoInfo> nextBooking = booking.get(NEXT);
        Map<Long, BookingDtoInfo> lastBooking = booking.get(LAST);
        return items.stream()
                .map(item -> {
                    BookingDtoInfo nextDto = getBookingDtoInfo(nextBooking, item.getId());
                    BookingDtoInfo lastDto = getBookingDtoInfo(lastBooking, item.getId());
                    List<CommentDto> commentDtoList = getCommentDtoList(commentsItem, item.getId());
                    return itemMapper.toOneItemDtoInfoForOwner(item, nextDto, lastDto, commentDtoList);
                }).sorted(Comparator.comparing(ItemDtoInfo::getId))
                .collect(Collectors.toList());
    }

    private BookingDtoInfo getBookingDtoInfo(Map<Long, BookingDtoInfo> bookingMap, Long itemId) {
        return bookingMap != null && bookingMap.containsKey(itemId) ? bookingMap.get(itemId) : null;
    }

    private List<CommentDto> getCommentDtoList(Map<Long, List<CommentDto>> commentsMap, Long itemId) {
        return commentsMap.containsKey(itemId) ? commentsMap.get(itemId) : new ArrayList<>();
    }

    private Collection<ItemDtoInfo> setBookingForOwner(List<Item> items, List<Long> itemId, Map<Long, List<CommentDto>> commentsItem) {
        LocalDateTime current = LocalDateTime.now();
        List<Booking> nextBookings = bookingRepository.findNextBookingsForOwner(current, itemId, APPROVED);
        List<Booking> lastBookings = bookingRepository.findLastBookingsForOwner(current, itemId, APPROVED);
        return getItemDtoInfoForOwner(items, nextBookings, lastBookings, commentsItem);
    }

    private Map<Long, List<CommentDto>> getCommentDtoSortByIdItem(List<Comment> comments) {
        List<CommentDto> allCommentDtoItems = commentMapper.toCommentDtoList(comments);
        Map<Long, List<CommentDto>> result = new HashMap<>();
        allCommentDtoItems.forEach(commentDto -> {
            List<CommentDto> comment = new ArrayList<>();
            if (result.containsKey(commentDto.getItemId())) {
                comment = result.get(commentDto.getItemId());
                comment.add(commentDto);
            } else {
                comment.add(commentDto);
            }
            result.put(commentDto.getItemId(), comment);
        });
        return result;
    }

    private void isBookerOfThisItem(Long userId, Long itemId) {
        boolean isValid = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, APPROVED, LocalDateTime.now());
        if (!isValid) {
            throw new ValidationException("Only users whose booking has expired can leave comments");
        }
    }
}
