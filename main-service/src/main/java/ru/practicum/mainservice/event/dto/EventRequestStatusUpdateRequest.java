package ru.practicum.mainservice.event.dto;

import lombok.Data;
import ru.practicum.mainservice.request.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
