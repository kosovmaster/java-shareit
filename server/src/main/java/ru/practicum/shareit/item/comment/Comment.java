package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String text;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;
    @JoinColumn(name = "user_id")
    @ManyToOne
    @ToString.Exclude
    private User user;
}
