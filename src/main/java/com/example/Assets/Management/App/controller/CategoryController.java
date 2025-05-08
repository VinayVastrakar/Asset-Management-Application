package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Assets.Management.App.dto.requestDto.CategoryRequestDTO;


import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Categories APIs")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all Categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category By Id")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create Categories")
    public Category createCategory(@RequestBody CategoryRequestDTO category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Categories by Id")
    public Category updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Categories")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
