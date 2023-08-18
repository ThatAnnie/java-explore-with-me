package ru.practicum.mainservice.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;
    @NotBlank
    @Column(name = "text")
    String text;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    @NotNull
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "edited_on")
    LocalDateTime editedOn;
}
