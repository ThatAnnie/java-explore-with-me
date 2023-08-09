package ru.practicum.mainservice.event.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.Location;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.EventState;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "lat", source = "eventDto.location.lat")
    @Mapping(target = "lon", source = "eventDto.location.lon")
    @Mapping(target = "paid", source = "eventDto.paid", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "requestModeration", source = "eventDto.requestModeration", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "participantLimit", source = "eventDto.participantLimit", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Event toEvent(NewEventDto eventDto, Category category, User initiator, EventState state, LocalDateTime createdOn);

    EventFullDto toEventFullDto(Event event, Location location);

    List<EventShortDto> toEventsShortDto(List<Event> events);

    EventShortDto toEventShortDto(Event event);
}
