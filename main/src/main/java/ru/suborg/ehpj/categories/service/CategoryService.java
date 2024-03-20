package ru.suborg.ehpj.categories.service;

import ru.suborg.ehpj.categories.dto.CategoryDto;
import ru.suborg.ehpj.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);
}
