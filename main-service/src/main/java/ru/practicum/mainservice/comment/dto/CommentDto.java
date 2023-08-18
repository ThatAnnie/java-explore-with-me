package ru.practicum.mainservice.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String text;
    UserShortDto author;
    EventShortDto event;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime createdOn;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime editedOn;
}
