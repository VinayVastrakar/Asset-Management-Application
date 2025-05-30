package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Assets.Management.App.dto.requestDto.CategoryRequestDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity createCategory(@RequestBody CategoryRequestDTO category) {
        Map<String,Object> res = new HashMap<>();
        res.put("data", categoryService.createCategory(category));
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Categories by Id")
    public ResponseEntity updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO category) {
       
        Map<String, Object> response = new HashMap<>();
        response.put("data", categoryService.updateCategory(id, category));
        response.put("message", "Update Successfull");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Categories")
    public ResponseEntity deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Delete Successfull" );
        return ResponseEntity.ok(response);
    }
}
