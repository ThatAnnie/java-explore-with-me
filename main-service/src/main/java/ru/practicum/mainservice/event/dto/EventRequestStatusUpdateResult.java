package ru.practicum.mainservice.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
