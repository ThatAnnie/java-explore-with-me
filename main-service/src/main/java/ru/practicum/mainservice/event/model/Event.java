package ru.practicum.mainservice.event.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;
    @NotBlank
    @Column(name = "title")
    String title;
    @NotBlank
    @Column(name = "annotation")
    String annotation;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @NotBlank
    @Column(name = "description")
    String description;
    @NotNull
    LocalDateTime eventDate;
    @NotNull
    @Column(name = "lat")
    Float lat;
    @NotNull
    @Column(name = "lon")
    Float lon;
    LocalDateTime createdOn;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;
    @Column(name = "paid")
    Boolean paid = false;
    @Column(name = "participant_limit")
    Integer participantLimit = 0;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;
}
