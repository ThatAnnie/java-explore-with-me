package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void addHit(StatsDto statsDto) {
        log.info("addHit: {}", statsDto);
        statsRepository.save(StatsMapper.INSTANCE.toStats(statsDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("getStats with: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты конца временного интервала.");
        }
        List<ViewStats> statsList;
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                statsList = statsRepository.getStatsUniqueIpWithoutUris(start, end);
            } else {
                statsList = statsRepository.getStatsNotUniqueWithoutUris(start, end);
            }
        } else if (unique) {
            statsList = statsRepository.getStatsUnique(start, end, uris);
        } else {
            statsList = statsRepository.getStatsNotUnique(start, end, uris);
        }
        return statsList.stream()
                .map(ViewStatsMapper.INSTANCE::toViewStatsDto)
                .collect(Collectors.toList());
    }
}
