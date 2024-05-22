package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findByItemId(Long itemId);

    Optional<List<Comment>> findAllByItemIdIn(List<Long> itemId);
}
