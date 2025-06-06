package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final AssetRepository assetRepository;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository, AssetRepository assetRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.assetRepository = assetRepository;
    }

    public List<PurchaseHistoryResponseDTO> getAll() {
        return purchaseHistoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PurchaseHistoryResponseDTO> getByAssetId(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with ID: " + assetId));

        return purchaseHistoryRepository.findByAsset(asset).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PurchaseHistoryResponseDTO create(PurchaseHistoryRequestDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId()).orElseThrow();
        PurchaseHistory ph = new PurchaseHistory();
        ph.setAsset(asset);
        ph.setPurchaseDate(dto.getPurchaseDate());
        ph.setPurchasePrice(dto.getAmount());
        ph.setVendorName(dto.getVendor());
        PurchaseHistory saved = purchaseHistoryRepository.save(ph);
        return toDTO(saved);
    }

    public void delete(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    private PurchaseHistoryResponseDTO toDTO(PurchaseHistory ph) {
        PurchaseHistoryResponseDTO dto = new PurchaseHistoryResponseDTO();
        dto.setId(ph.getId());
        dto.setAssetId(ph.getAsset().getId());
        dto.setAssetName(ph.getAsset().getName());
        dto.setPurchaseDate(ph.getPurchaseDate());
        dto.setAmount(ph.getPurchasePrice());
        dto.setVendor(ph.getVendorName());
        return dto;
    }
}
