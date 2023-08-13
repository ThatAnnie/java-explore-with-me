package ru.practicum.mainservice.comment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoRequest {
    @NotBlank
    @Size(min = 1, max = 2000)
    String text;
}
