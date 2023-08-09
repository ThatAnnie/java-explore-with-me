package ru.practicum.mainservice.category.service;

import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto categoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);
}
