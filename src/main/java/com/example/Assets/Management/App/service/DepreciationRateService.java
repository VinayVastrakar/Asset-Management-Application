package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.requestDto.DepreciationRateRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.DepreciationRateResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PaginatedResponse;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.DepreciationRate;
import com.example.Assets.Management.App.repository.CategoryRepository;
import com.example.Assets.Management.App.repository.DepreciationRateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepreciationRateService {
    private final DepreciationRateRepository depreciationRateRepository;
    private final CategoryRepository categoryRepository;

    public DepreciationRateService(DepreciationRateRepository depreciationRateRepository, CategoryRepository categoryRepository) {
        this.depreciationRateRepository = depreciationRateRepository;
        this.categoryRepository = categoryRepository;
    }

    public DepreciationRateResponseDTO create(DepreciationRateRequestDTO dto) {
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
        return toResponseDTO(saved);
    }

    public List<DepreciationRateResponseDTO> listAll() {
        return depreciationRateRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public DepreciationRateResponseDTO getById(Long id) {
        DepreciationRate rate = depreciationRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found"));
        return toResponseDTO(rate);
    }

    public DepreciationRateResponseDTO update(Long id, DepreciationRateRequestDTO dto) {
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
        return toResponseDTO(saved);
    }

    public void delete(Long id) {
        if (!depreciationRateRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found");
        }
        depreciationRateRepository.deleteById(id);
    }
    public DepreciationRateResponseDTO getByCategoryIdAndFinancialYear(Long categoryId, String financialYear) {
        DepreciationRate rate = depreciationRateRepository.findByCategoryIdAndFinancialYear(categoryId, financialYear)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depreciation rate not found"));
        return toResponseDTO(rate);
    }

    public PaginatedResponse<DepreciationRateResponseDTO> getPaginated(int page, int limit, Long categoryId, String financialYear) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<DepreciationRate> pageResult;
        if (categoryId != null && financialYear != null) {
            pageResult = depreciationRateRepository.findByCategoryIdAndFinancialYear(categoryId, financialYear, pageable);
        } else if (categoryId != null) {
            pageResult = depreciationRateRepository.findByCategoryId(categoryId, pageable);
        } else if (financialYear != null) {
            pageResult = depreciationRateRepository.findByFinancialYear(financialYear, pageable);
        } else {
            pageResult = depreciationRateRepository.findAll(pageable);
        }
        List<DepreciationRateResponseDTO> dtos = pageResult.getContent().stream().map(this::toResponseDTO).collect(Collectors.toList());
        return new PaginatedResponse<>(dtos, pageResult.getTotalElements(), pageResult.getNumber(), pageResult.getSize());
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