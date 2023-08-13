package ru.practicum.mainservice.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainservice.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

}
