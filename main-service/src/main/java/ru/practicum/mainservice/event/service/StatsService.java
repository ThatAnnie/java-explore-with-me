package ru.practicum.mainservice.event.service;

import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    void addHit(HttpServletRequest httpRequest);
}
