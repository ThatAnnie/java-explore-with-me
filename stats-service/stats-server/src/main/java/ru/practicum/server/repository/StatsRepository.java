package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.Stats;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("select new ru.practicum.server.model.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where (s.timestamp between :start and :end) " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ViewStats> getStatsUniqueIpWithoutUris(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.server.model.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Stats s " +
            "where (s.timestamp between :start and :end) " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getStatsNotUniqueWithoutUris(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.server.model.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where (s.timestamp between :start and :end) " +
            "and s.uri IN (:uris) " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ViewStats> getStatsUnique(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") List<String> uris);

    @Query("select new ru.practicum.server.model.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Stats s " +
            "where (s.timestamp between :start and :end) " +
            "and s.uri IN (:uris) " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getStatsNotUnique(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);
}