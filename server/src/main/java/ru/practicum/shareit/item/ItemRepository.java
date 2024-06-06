package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByIdAndOwner_Id(Long id, Long ownerId);

    List<Item> findByAvailableTrueAndDescriptionContainsIgnoreCaseOrAvailableTrueAndNameContainsIgnoreCase(String text,
                                                                                                           String textDuplicate,
                                                                                                           Pageable pageable);
}
