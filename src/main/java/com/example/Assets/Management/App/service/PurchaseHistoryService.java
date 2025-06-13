package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.mapper.PurchaseHistoryMapper;
import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final AssetRepository assetRepository;
    private final PurchaseHistoryMapper purchaseHistoryMapper;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository, 
                                AssetRepository assetRepository,
                                PurchaseHistoryMapper purchaseHistoryMapper) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.assetRepository = assetRepository;
        this.purchaseHistoryMapper = purchaseHistoryMapper;
    }

    public Page<PurchaseHistoryResponseDTO> getAll(int page, int size, String[] sort) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        return purchaseHistoryRepository.findAll(pageable)
                .map(purchaseHistoryMapper::toResponseDTO);
    }

    public Page<PurchaseHistoryResponseDTO> getByAssetId(Long assetId, int page, int size, String[] sort) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with ID: " + assetId));

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        return purchaseHistoryRepository.findByAsset(asset, pageable)
                .map(purchaseHistoryMapper::toResponseDTO);
    }

    public PurchaseHistoryResponseDTO create(PurchaseHistoryRequestDTO requestDto, Users u) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        
        PurchaseHistory ph = purchaseHistoryMapper.fromRequestDTO(requestDto);
        ph.setAsset(asset);
        ph.setLastChangeBy(u);
        PurchaseHistory saved = purchaseHistoryRepository.save(ph);
        return purchaseHistoryMapper.toResponseDTO(saved);
    }

    public void delete(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    public PurchaseHistoryResponseDTO getById(Long id) {
        PurchaseHistory purchaseHistory = purchaseHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));
        return purchaseHistoryMapper.toResponseDTO(purchaseHistory);
    }

    public PurchaseHistoryResponseDTO update(Long id, PurchaseHistoryRequestDTO requestDto, Users user) {
        PurchaseHistory existingHistory = purchaseHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));

        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));

        // Update the existing purchase history with new values
        existingHistory.setAsset(asset);
        existingHistory.setPurchaseDate(requestDto.getPurchaseDate());
        existingHistory.setPurchasePrice(requestDto.getAmount());
        existingHistory.setVendorName(requestDto.getVendor());
        existingHistory.setInvoiceNumber(requestDto.getInvoiceNumber());
        existingHistory.setWarrantyPeriod(requestDto.getWarrantyPeriod());
        existingHistory.setExpiryDate(requestDto.getExpiryDate());
        existingHistory.setNotify(requestDto.getNotify());
        existingHistory.setDescription(requestDto.getDescription());
        existingHistory.setLastChangeBy(user);

        PurchaseHistory updated = purchaseHistoryRepository.save(existingHistory);
        return purchaseHistoryMapper.toResponseDTO(updated);
    }

    // Optional: Keep this method if you still need it elsewhere
    private PurchaseHistoryResponseDTO toDTO(PurchaseHistory ph) {
        return purchaseHistoryMapper.toResponseDTO(ph);
    }
}