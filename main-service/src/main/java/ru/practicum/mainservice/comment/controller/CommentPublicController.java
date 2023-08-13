package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByEvent(@RequestParam Long eventId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentsByEvent(eventId, from, size);
    }
}
