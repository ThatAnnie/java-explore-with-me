package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus confirmed);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query("select count(r) from Request r where r.status = 'CONFIRMED' and r.event.id IN (:eventId)")
    Long countConfirmedByEventId(Long eventId);
}
