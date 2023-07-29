package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.StatsConstants.DATE_TIME_FORMATTER;

public class StatsClient {
    private final WebClient webClient;


    public StatsClient(@Value("${stats-server.url}") String url) {
        this.webClient = WebClient.create(url);
    }

    public void addHit(StatsDto statsDto) {
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(statsDto)
                .retrieve()
                .bodyToMono(StatsDto.class)
                .block();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(DATE_TIME_FORMATTER))
                        .queryParam("end", end.format(DATE_TIME_FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }
}