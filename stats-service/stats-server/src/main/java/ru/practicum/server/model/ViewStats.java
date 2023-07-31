package ru.practicum.server.model;

import lombok.*;

@Data
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
