package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.validator.FutureEventDate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @FutureEventDate
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    @NotNull
    @Valid
    Location location;
    boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
