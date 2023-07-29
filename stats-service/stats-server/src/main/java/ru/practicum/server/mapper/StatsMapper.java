package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.model.Stats;

@Mapper
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Stats toStats(StatsDto statsDto);
}
