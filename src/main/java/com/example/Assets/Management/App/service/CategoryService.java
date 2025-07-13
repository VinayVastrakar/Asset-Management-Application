package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.repository.CategoryRepository;
import com.example.Assets.Management.App.exception.DuplicateCategoryException;
import org.springframework.stereotype.Service;
import com.example.Assets.Management.App.dto.requestDto.CategoryRequestDTO;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(CategoryRequestDTO category) {
        // Check if category with same name already exists (case-insensitive)
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new DuplicateCategoryException("Category with name '" + category.getName() + "' already exists");
        }
        
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        return categoryRepository.save(newCategory);
    }

    public Category updateCategory(Long id, CategoryRequestDTO category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if the new name conflicts with another category (excluding current one)
        Optional<Category> existingWithSameName = categoryRepository.findByNameIgnoreCase(category.getName());
        if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(id)) {
            throw new DuplicateCategoryException("Category with name '" + category.getName() + "' already exists");
        }
        
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
