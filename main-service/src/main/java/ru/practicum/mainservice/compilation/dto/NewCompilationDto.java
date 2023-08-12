package ru.practicum.mainservice.compilation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
    Boolean pinned;
    List<Long> events;
}
