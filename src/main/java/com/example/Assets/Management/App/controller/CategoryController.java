package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.service.CategoryService;
import com.example.Assets.Management.App.exception.DuplicateCategoryException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.Assets.Management.App.dto.requestDto.CategoryRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Categories APIs")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
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
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create Categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO category) {
        logger.info("Creating category: {}", category.getName());
        try {
            Map<String,Object> res = new HashMap<>();
            res.put("data", categoryService.createCategory(category));
            res.put("message", "Category created successfully");
            logger.info("Category created successfully: {}", category.getName());
            return ResponseEntity.ok(res);
        } catch (DuplicateCategoryException e) {
            logger.warn("Duplicate category attempt: {}", e.getMessage());
            Map<String,Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            Map<String,Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An error occurred while creating the category");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Categories by Id")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO category) {
        logger.info("Updating category with id: {}, new name: {}", id, category.getName());
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("data", categoryService.updateCategory(id, category));
            response.put("message", "Update Successful");
            logger.info("Category updated successfully: {}", category.getName());
            return ResponseEntity.ok(response);
        } catch (DuplicateCategoryException e) {
            logger.warn("Duplicate category attempt during update: {}", e.getMessage());
            Map<String,Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
            Map<String,Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An error occurred while updating the category");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Categories")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Delete Successful" );
        return ResponseEntity.ok(response);
    }
}
