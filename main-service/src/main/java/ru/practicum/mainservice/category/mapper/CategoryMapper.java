package ru.practicum.mainservice.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.model.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoriesDto(List<Category> categories);
}
