package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByRequester(Long userId);

    EventRequestStatusUpdateResult updateRequestByOwner(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getEventRequestsByOwner(Long userId, Long eventId);
}
