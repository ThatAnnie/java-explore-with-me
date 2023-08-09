package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsClient statsClient;

    @Value(value = "${spring.application.name}")
    private String appName;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("getStats with: rangeStart={}, rangeEnd={}, uris={}, unique={}", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }

    @Override
    public void addHit(HttpServletRequest httpRequest) {
        StatsDto statsDto = new StatsDto(appName, httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr(), LocalDateTime.now());
        statsClient.addHit(statsDto);
    }
}
