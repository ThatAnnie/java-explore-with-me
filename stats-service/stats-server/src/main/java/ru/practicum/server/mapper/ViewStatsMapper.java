package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.ViewStats;

@Mapper
public interface ViewStatsMapper {
    ViewStatsMapper INSTANCE = Mappers.getMapper(ViewStatsMapper.class);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);

}