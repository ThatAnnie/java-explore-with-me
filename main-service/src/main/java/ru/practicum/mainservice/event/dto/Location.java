package ru.practicum.mainservice.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Location {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
