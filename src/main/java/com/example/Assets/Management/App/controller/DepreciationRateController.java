package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.DepreciationRateRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.DepreciationRateResponseDTO;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.DepreciationRate;
import com.example.Assets.Management.App.repository.CategoryRepository;
import com.example.Assets.Management.App.repository.DepreciationRateRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/depreciation-rates")
public class DepreciationRateController {
    private final DepreciationRateRepository depreciationRateRepository;
    private final CategoryRepository categoryRepository;

    public DepreciationRateController(DepreciationRateRepository depreciationRateRepository, CategoryRepository categoryRepository) {
        this.depreciationRateRepository = depreciationRateRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<DepreciationRateResponseDTO> create(@Valid @RequestBody DepreciationRateRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        DepreciationRate rate = DepreciationRate.builder()
                .category(category)
                .assetType(dto.getAssetType())
                .financialYear(dto.getFinancialYear())
                .depreciationPercentage(dto.getDepreciationPercentage())
                .depreciationMethod(dto.getDepreciationMethod())
                .usefulLifeYears(dto.getUsefulLifeYears())
                .residualValuePercentage(dto.getResidualValuePercentage())
                .effectiveFromDate(dto.getEffectiveFromDate())
                .effectiveToDate(dto.getEffectiveToDate())
                .build();
        DepreciationRate saved = depreciationRateRepository.save(rate);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<DepreciationRateResponseDTO>> listAll() {
        List<DepreciationRate> rates = depreciationRateRepository.findAll();
        List<DepreciationRateResponseDTO> dtos = rates.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepreciationRateResponseDTO> getById(@PathVariable Long id) {
        DepreciationRate rate = depreciationRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found"));
        return ResponseEntity.ok(toResponseDTO(rate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepreciationRateResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DepreciationRateRequestDTO dto) {
        DepreciationRate rate = depreciationRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        rate.setCategory(category);
        rate.setAssetType(dto.getAssetType());
        rate.setFinancialYear(dto.getFinancialYear());
        rate.setDepreciationPercentage(dto.getDepreciationPercentage());
        rate.setDepreciationMethod(dto.getDepreciationMethod());
        rate.setUsefulLifeYears(dto.getUsefulLifeYears());
        rate.setResidualValuePercentage(dto.getResidualValuePercentage());
        rate.setEffectiveFromDate(dto.getEffectiveFromDate());
        rate.setEffectiveToDate(dto.getEffectiveToDate());
        DepreciationRate saved = depreciationRateRepository.save(rate);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!depreciationRateRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found");
        }
        depreciationRateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DepreciationRateResponseDTO toResponseDTO(DepreciationRate rate) {
        DepreciationRateResponseDTO dto = new DepreciationRateResponseDTO();
        dto.setId(rate.getId());
        dto.setCategoryId(rate.getCategory().getId());
        dto.setCategoryName(rate.getCategory().getName());
        dto.setAssetType(rate.getAssetType());
        dto.setFinancialYear(rate.getFinancialYear());
        dto.setDepreciationPercentage(rate.getDepreciationPercentage());
        dto.setDepreciationMethod(rate.getDepreciationMethod());
        dto.setUsefulLifeYears(rate.getUsefulLifeYears());
        dto.setResidualValuePercentage(rate.getResidualValuePercentage());
        dto.setEffectiveFromDate(rate.getEffectiveFromDate());
        dto.setEffectiveToDate(rate.getEffectiveToDate());
        dto.setCreatedAt(rate.getCreatedAt());
        dto.setUpdatedAt(rate.getUpdatedAt());
        return dto;
    }
} 