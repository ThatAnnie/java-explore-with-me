package ru.practicum.mainservice.comment.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.dto.CommentDtoRequest;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.model.QComment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.common.CustomPageRequest;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.EventState;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CommentDtoRequest newCommentDto) {
        log.info("createComment by userId={} to event with id={}: {}", userId, eventId, newCommentDto);
        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentDtoRequest commentDto) {
        log.info("updateComment with id={} by userId={}: {}", commentId, userId, commentDto);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("comment with id={} not exist", userId);
            throw new NotFoundException(String.format("Comment with id=%d was not found", commentId));
        });
        if (!userId.equals(comment.getAuthor().getId())) {
            log.warn("user with id={} is not author of comment", userId);
            throw new NotFoundException(String.format("User with id=%d is not author of comment", userId));
        }
        if (comment.getCreatedOn().isBefore(LocalDateTime.now().minusDays(2))) {
            throw new ConflictException("Comment can be edited during 2 days");
        }
        comment.setEditedOn(LocalDateTime.now());
        comment.setText(commentDto.getText());
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("deleteComment with id={} by userId={}", commentId, userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("comment with id={} not exist", commentId);
            throw new NotFoundException(String.format("Comment with id=%d was not found", commentId));
        });
        if (!userId.equals(comment.getAuthor().getId())) {
            log.warn("user with id={} is not author of comment", userId);
            throw new NotFoundException(String.format("User with id=%d is not author of comment", userId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByUser(Long userId, Integer from, Integer size) {
        log.info("getCommentsByUser with id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        PageRequest pageRequest = new CustomPageRequest(from, size);
        return CommentMapper.INSTANCE.toCommentsDto(commentRepository.findAllByAuthorId(userId, pageRequest));
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("getCommentById with id={}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.warn("comment with id={} not exist", commentId);
            throw new NotFoundException(String.format("Comment with id=%d was not found", commentId));
        });
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size) {
        log.info("getCommentsByEvent with id={}", eventId);
        eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("event with id={} not exist", eventId);
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        PageRequest pageRequest = new CustomPageRequest(from, size);
        return CommentMapper.INSTANCE.toCommentsDto(commentRepository.findAllByEventId(eventId, pageRequest));
    }

    @Override
    public List<CommentDto> getCommentsByAdmin(List<Long> users, List<Long> events, LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.warn("rangeStart={} is after rangeEnd={}", rangeStart, rangeEnd);
            throw new ConflictException(String.format("rangeStart={} is before rangeEnd={}", rangeStart, rangeEnd));
        }
        PageRequest pageRequest = new CustomPageRequest(from, size);
        QComment qComment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();
        if (users != null && !users.isEmpty()) {
            builder.and(qComment.author.id.in(users));
        }
        if (events != null && !events.isEmpty()) {
            builder.and(qComment.event.id.in(events));
        }
        if (rangeStart != null) {
            builder.and(qComment.createdOn.goe(rangeStart));
        }
        if (rangeEnd != null) {
            builder.and(qComment.createdOn.loe(rangeEnd));
        }
        List<Comment> comments = commentRepository.findAll(builder, pageRequest).stream().collect(Collectors.toList());
        return CommentMapper.INSTANCE.toCommentsDto(comments);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info("deleteCommentByAdmin with commentId={}", commentId);
        commentRepository.deleteById(commentId);
    }
}
