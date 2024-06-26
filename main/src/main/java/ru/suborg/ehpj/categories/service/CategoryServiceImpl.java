package ru.suborg.ehpj.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.suborg.ehpj.categories.Category;
import ru.suborg.ehpj.categories.CategoryMapper;
import ru.suborg.ehpj.categories.CategoryRepository;
import ru.suborg.ehpj.categories.dto.CategoryDto;
import ru.suborg.ehpj.categories.dto.NewCategoryDto;
import ru.suborg.ehpj.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = getCategory(categoryId);
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }
        categoryRepository.deleteById(categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category with id=" + categoryId + " was not found"));
    }
}
