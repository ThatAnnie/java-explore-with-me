package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.dto.CommentDtoRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, CommentDtoRequest newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, CommentDtoRequest commentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getCommentsByUser(Long userId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsByAdmin(List<Long> users, List<Long> events, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Integer from, Integer size);

    void deleteCommentByAdmin(Long commentId);
}
