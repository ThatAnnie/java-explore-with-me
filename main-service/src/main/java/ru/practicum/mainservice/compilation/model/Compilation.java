package ru.practicum.mainservice.compilation.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Event;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    Long id;
    @Column(name = "pinned")
    Boolean pinned = false;
    @NotNull
    @Column(name = "title")
    String title;
    @ManyToMany
    @JoinTable(name = "compilation_event", joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    List<Event> events;
}
