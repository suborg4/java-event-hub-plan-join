package ru.suborg.ehpj.categories;

import ru.suborg.ehpj.categories.dto.CategoryDto;
import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.categories.dto.NewCategoryDto;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(newCategoryDto.getName());
    }

    public Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
