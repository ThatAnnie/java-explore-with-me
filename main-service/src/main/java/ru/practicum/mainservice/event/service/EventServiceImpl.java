package ru.practicum.mainservice.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.common.CustomPageRequest;
import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.EventState;
import ru.practicum.mainservice.event.model.QEvent;
import ru.practicum.mainservice.event.model.SortType;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.BadRequestException;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        log.info("createEvent by userId={}: {}", userId, eventDto);
        User initiator = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> {
            log.warn("category with id={} not exist", eventDto.getCategory());
            throw new NotFoundException(String.format("Category with id=%d was not found", eventDto.getCategory()));
        });
        Event event = EventMapper.INSTANCE.toEvent(eventDto, category, initiator, EventState.PENDING, LocalDateTime.now());
        Location location = new Location(event.getLat(), event.getLon());
        return EventMapper.INSTANCE.toEventFullDto(eventRepository.save(event), location);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("updateEventByUser with eventId={} by userId={}: {}", eventId, userId, updateEventUserRequest);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (!userId.equals(event.getInitiator().getId())) {
            log.warn("user with id{} is not initiator of event with id={} not exist", userId, eventId);
            throw new NotFoundException(String.format("User with id=%d is not initiator of event with id={}", userId, eventId));
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(String.format("Less then 2 hour before event"));
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventUserRequest.getAnnotation() != null && !updateEventUserRequest.getAnnotation().isEmpty()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> {
                log.warn("category with id={} not exist", updateEventUserRequest.getCategory());
                throw new NotFoundException(String.format("Category with id=%d was not found", updateEventUserRequest.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getAnnotation().isEmpty()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLon(updateEventUserRequest.getLocation().getLon());
            event.setLat(updateEventUserRequest.getLocation().getLat());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getAnnotation().isEmpty()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return EventMapper.INSTANCE.toEventFullDto(event, new Location(event.getLat(), event.getLon()));
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        log.info("getEventsByUser with from={}, size={}, userId={}", from, size, userId);
        PageRequest pageRequest = new CustomPageRequest(from, size);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        return events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.INSTANCE.toEventShortDto(event);
                    eventShortDto.setConfirmedRequests(requestRepository.countConfirmedByEventId(event.getId()));
                    eventShortDto.setViews(getViewsStatsByEvent(event));
                    return eventShortDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        return EventMapper.INSTANCE.toEventFullDto(event, new Location(event.getLat(), event.getLon()));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                               Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.warn("rangeStart={} is after rangeEnd={}", rangeStart, rangeEnd);
            throw new ConflictException(String.format("rangeStart={} is before rangeEnd={}", rangeStart, rangeEnd));
        }

        PageRequest pageRequest = new CustomPageRequest(from, size);
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        if (users != null && !users.isEmpty()) {
            builder.and(qEvent.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            builder.and(qEvent.state.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            builder.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }
        List<Event> events = eventRepository.findAll(builder, pageRequest).stream().collect(Collectors.toList());

        return events.stream()
                .map(event -> {
                    EventFullDto eventFullDto = EventMapper.INSTANCE.toEventFullDto(event, new Location(event.getLat(), event.getLon()));
                    eventFullDto.setConfirmedRequests(requestRepository.countConfirmedByEventId(event.getId()));
                    eventFullDto.setViews(getViewsStatsByEvent(event));
                    return eventFullDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("updateEventByAdmin with id={}: {} ", eventId, updateEventAdminRequest);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(String.format("Less then 1 hour before event"));
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Only pending events can be changed");
            }
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> {
                log.warn("category with id={} not exist", updateEventAdminRequest.getCategory());
                throw new NotFoundException(String.format("Category with id=%d was not found", updateEventAdminRequest.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLon(updateEventAdminRequest.getLocation().getLon());
            event.setLat(updateEventAdminRequest.getLocation().getLat());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return EventMapper.INSTANCE.toEventFullDto(event, new Location(event.getLat(), event.getLon()));
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                 SortType sort, Integer from, Integer size, HttpServletRequest httpRequest) {
        log.info("getEventsByPublic with text={}, categories={}, paid={}, rangeStart={}, rangeEnd=[], " +
                        "onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.warn("rangeStart={} is after rangeEnd={}", rangeStart, rangeEnd);
            throw new BadRequestException(String.format("rangeStart={} is before rangeEnd={}", rangeStart, rangeEnd));
        }
        PageRequest pageRequest = new CustomPageRequest(from, size);
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.state.in(EventState.PUBLISHED));
        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            builder.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }
        if (text != null) {
            builder.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (paid != null) {
            builder.and(qEvent.paid.eq(paid));
        }
        List<Event> events = eventRepository.findAll(builder, pageRequest).stream().collect(Collectors.toList());

        statsService.addHit(httpRequest);
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getParticipantLimit() > requestRepository.countConfirmedByEventId(event.getId()))
                    .collect(Collectors.toList());
        }


        List<EventShortDto> eventsShortDto = events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.INSTANCE.toEventShortDto(event);
                    eventShortDto.setConfirmedRequests(requestRepository.countConfirmedByEventId(event.getId()));
                    eventShortDto.setViews(getViewsStatsByEvent(event));
                    return eventShortDto;
                })
                .collect(Collectors.toList());

        if (sort != null) {
            switch (sort) {
                case VIEWS:
                    eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                case EVENT_DATE:
                    eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
            }
        }

        return eventsShortDto;
    }

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        log.info("getEventByPublic with id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event is not published");
        }
        EventFullDto eventFullDto = EventMapper.INSTANCE.toEventFullDto(event, new Location(event.getLat(), event.getLon()));
        eventFullDto.setConfirmedRequests(requestRepository.countConfirmedByEventId(event.getId()));
        eventFullDto.setViews(getViewsStatsByEvent(event));
        statsService.addHit(request);
        return eventFullDto;
    }

    private Long getViewsStatsByEvent(Event event) {
        String uri = "/events/" + event.getId().toString();
        List<String> uris = List.of(uri);
        List<ViewStatsDto> viewStats = statsService.getStats(event.getCreatedOn(),
                LocalDateTime.now().plusYears(1L), uris, true);
        Map<Long, Long> map = viewStats.stream()
                .filter(stat -> stat.getApp().equals("main-service"))
                .collect(Collectors.toMap(
                        stat -> Long.parseLong(stat.getUri().split("/", 0)[2]),
                                ViewStatsDto::getHits
                        )
                );
        return map.getOrDefault(event.getId(), 0L);
    }
}
