package ru.practicum.mainservice.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CategoryDto {
    @NotNull
    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
