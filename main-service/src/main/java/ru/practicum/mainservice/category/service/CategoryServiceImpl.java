package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.common.CustomPageRequest;
import ru.practicum.mainservice.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        log.info("createCategory: {}", categoryDto);
        return CategoryMapper.INSTANCE.toCategoryDto(categoryRepository.save(CategoryMapper.INSTANCE.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("update category with id={}: {}", catId, categoryDto);

        Category updateCategory = categoryRepository.findById(catId).orElseThrow(() -> {
            log.warn("category with id={} not exist", catId);
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        });
        updateCategory.setName(categoryDto.getName());
        return CategoryMapper.INSTANCE.toCategoryDto(updateCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("deleteCategory with id={}", catId);
        categoryRepository.findById(catId).orElseThrow(() -> {
            log.warn("category with id={} not exist", catId);
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        });
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("getCategories with from={}, size={}", from, size);
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Category> categories = categoryRepository.findAll(pageRequest).getContent();
        return CategoryMapper.INSTANCE.toCategoriesDto(categories);
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        log.info("getCategory with id={}", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.warn("category with id={} not exist", catId);
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        });
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }
}
