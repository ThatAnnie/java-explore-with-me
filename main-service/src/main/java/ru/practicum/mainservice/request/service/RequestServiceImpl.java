package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.EventState;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.RequestStatus;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.mainservice.request.model.RequestStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            log.warn("request already exist");
            throw new ConflictException("Request already exist");
        }
        if (userId.equals(event.getInitiator().getId())) {
            log.warn("can't create request by initiator");
            throw new ConflictException("Request can't be created by event initiator");
        }
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("event is not published");
            throw new ConflictException("Event is not published");
        }
        if (event.getParticipantLimit() <= requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size() && event.getParticipantLimit() != 0) {
            log.warn("limit of participants was reached");
            throw new ConflictException("Limit of participants was reached");
        }
        RequestStatus status = PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = CONFIRMED;
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(status)
                .build();
        return RequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("request with id={} not exist", requestId);
            throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
        });
        if (userId.equals(request.getEvent().getInitiator().getId())) {
            log.warn("can't create request by initiator");
            throw new ConflictException("Request can't be created by event initiator");
        }
        request.setStatus(CANCELED);
        return RequestMapper.INSTANCE.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByRequester(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        return RequestMapper.INSTANCE.toParticipationRequestsDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestByOwner(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("updateRequestByOwner: userId={}, eventId={}, statusUpdateRequest", userId, eventId, statusUpdateRequest);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (!userId.equals(event.getInitiator().getId())) {
            log.warn("can't change request's status by not initiator");
            throw new ConflictException("Can't change request's status by not initiator");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }
        List<Long> requestIds = statusUpdateRequest.getRequestIds();
        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedList = new ArrayList<>();
        for (Long requestId : requestIds) {
            Request request = requestRepository.findById(requestId).orElseThrow(() -> {
                log.warn("request with id={} not exist", requestId);
                throw new NotFoundException(String.format("Request with id=%d was not found", requestId));
            });
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException(String.format("Can't change status of request with id={}", requestId));
            }
            if (statusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() <= requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size()) {
                    log.warn("limit of participants was reached");
                    throw new ConflictException("Limit of participants was reached");
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedList.add(RequestMapper.INSTANCE.toParticipationRequestDto(request));
                }
            } else if (statusUpdateRequest.getStatus() == REJECTED) {
                request.setStatus(REJECTED);
                rejectedList.add(RequestMapper.INSTANCE.toParticipationRequestDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedList, rejectedList);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByOwner(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (!userId.equals(event.getInitiator().getId())) {
            log.warn("can't get requests by not initiator");
            throw new ConflictException("Can't get requests by not initiator");
        }
        return RequestMapper.INSTANCE.toParticipationRequestsDto(requestRepository.findAllByEventId(eventId));
    }
}
