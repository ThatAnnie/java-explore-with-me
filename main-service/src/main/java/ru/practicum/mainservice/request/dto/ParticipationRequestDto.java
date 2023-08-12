package ru.practicum.mainservice.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.request.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.mainservice.common.CommonConstants.DATE_TIME_FORMAT;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime created;
    @NotNull
    Long event;
    @NotNull
    Long requester;
    @NotNull
    RequestStatus status;
}
