package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = ("/users/{userId}/events"))
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Validated @RequestBody NewEventDto eventDto) {
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestByOwner(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateRequestByOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByOwner(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        return requestService.getEventRequestsByOwner(userId, eventId);
    }
}
