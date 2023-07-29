package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.model.Stats;

import static ru.practicum.dto.StatsConstants.DATE_TIME_FORMAT;

@Mapper
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = DATE_TIME_FORMAT)
    Stats toStats(StatsDto statsDto);
}
