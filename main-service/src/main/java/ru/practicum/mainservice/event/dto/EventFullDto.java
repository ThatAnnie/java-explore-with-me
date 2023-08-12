package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.event.model.EventState;
import ru.practicum.mainservice.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime createdOn;
    String description;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    Long id;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Location location;
    @NotNull
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    @NotBlank
    String title;
    Long views;
}
