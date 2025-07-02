package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.DepreciationRateRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.ApiResponse;
import com.example.Assets.Management.App.dto.responseDto.DepreciationRateResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PaginatedResponse;
import com.example.Assets.Management.App.service.DepreciationRateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/depreciation-rates")
public class DepreciationRateController {
    private final DepreciationRateService depreciationRateService;

    public DepreciationRateController(DepreciationRateService depreciationRateService) {
        this.depreciationRateService = depreciationRateService;
    }

    @Operation(summary = "Create a new depreciation rate")
    @PostMapping
    public ResponseEntity<ApiResponse<DepreciationRateResponseDTO>> create(@Valid @RequestBody DepreciationRateRequestDTO dto) {
        DepreciationRateResponseDTO created = depreciationRateService.create(dto);
        ApiResponse<DepreciationRateResponseDTO> response = new ApiResponse<>(true, "Depreciation rate created successfully", created);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get paginated list of depreciation rates, optionally filtered by category and financial year")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<DepreciationRateResponseDTO>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String financialYear
    ) {
        PaginatedResponse<DepreciationRateResponseDTO> paginated = depreciationRateService.getPaginated(page, limit, categoryId, financialYear);
        ApiResponse<PaginatedResponse<DepreciationRateResponseDTO>> response = new ApiResponse<>(true, "Fetched successfully", paginated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get depreciation rate by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepreciationRateResponseDTO>> getById(@PathVariable Long id) {
        DepreciationRateResponseDTO dto = depreciationRateService.getById(id);
        ApiResponse<DepreciationRateResponseDTO> response = new ApiResponse<>(true, "Fetched successfully", dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update depreciation rate by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepreciationRateResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody DepreciationRateRequestDTO dto) {
        DepreciationRateResponseDTO updated = depreciationRateService.update(id, dto);
        ApiResponse<DepreciationRateResponseDTO> response = new ApiResponse<>(true, "Depreciation rate updated successfully", updated);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete depreciation rate by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        depreciationRateService.delete(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Depreciation rate deleted successfully", null);
        return ResponseEntity.ok(response);
    }
} 