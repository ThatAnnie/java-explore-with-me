package ru.practicum.mainservice.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    @NotNull
    Long id;
    @NotBlank
    String text;
    @NotNull
    UserShortDto author;
    @NotNull
    EventShortDto event;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime createdOn;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime editedOn;
}
