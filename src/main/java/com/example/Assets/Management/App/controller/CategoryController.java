package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Assets.Management.App.dto.requestDto.CategoryRequestDTO;


import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Category createCategory(@RequestBody CategoryRequestDTO category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
