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
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final AssetRepository assetRepository;
    private final PurchaseHistoryMapper purchaseHistoryMapper;
    private final Cloudinary cloudinary;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository, 
                                AssetRepository assetRepository,
                                PurchaseHistoryMapper purchaseHistoryMapper,
                                Cloudinary cloudinary) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.assetRepository = assetRepository;
        this.purchaseHistoryMapper = purchaseHistoryMapper;
        this.cloudinary = cloudinary;
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
        
        return purchaseHistoryRepository.findByAssetId(assetId, pageable)
                .map(purchaseHistoryMapper::toResponseDTO);
    }

    public PurchaseHistoryResponseDTO createWithBill(PurchaseHistoryRequestDTO requestDto, MultipartFile file, Users u) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        PurchaseHistory ph = purchaseHistoryMapper.fromRequestDTO(requestDto);
        ph.setAsset(asset);
        ph.setLastChangeBy(u);
        if (file != null && !file.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "bills/",
                        "public_id", "bill_" + System.currentTimeMillis(),
                        "format", "pdf",                              // optional, enforces .pdf extension
                        "type", "upload"
                    )
                );
                ph.setBillUrl((String) uploadResult.get("secure_url"));
                ph.setBillPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload bill PDF", e);
            }
        }
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

    // public PurchaseHistoryResponseDTO update(Long id, PurchaseHistoryRequestDTO requestDto, Users user) {
    //     PurchaseHistory existingHistory = purchaseHistoryRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));

    //     Asset asset = assetRepository.findById(requestDto.getAssetId())
    //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));

    //     // Update the existing purchase history with new values
    //     existingHistory.setAsset(asset);
    //     existingHistory.setPurchaseDate(requestDto.getPurchaseDate());
    //     existingHistory.setPurchasePrice(requestDto.getAmount());
    //     existingHistory.setVendorName(requestDto.getVendor());
    //     existingHistory.setInvoiceNumber(requestDto.getInvoiceNumber());
    //     existingHistory.setWarrantyPeriod(requestDto.getWarrantyPeriod());
    //     existingHistory.setExpiryDate(requestDto.getExpiryDate());
    //     existingHistory.setNotify(requestDto.getNotify());
    //     existingHistory.setDescription(requestDto.getDescription());
    //     existingHistory.setLastChangeBy(user);

    //     PurchaseHistory updated = purchaseHistoryRepository.save(existingHistory);
    //     return purchaseHistoryMapper.toResponseDTO(updated);
    // }

    public PurchaseHistoryResponseDTO updateWithBill(Long id, PurchaseHistoryRequestDTO requestDto, MultipartFile file, Users user) {
        PurchaseHistory existingHistory = purchaseHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        existingHistory.setAsset(asset);
        existingHistory.setPurchaseDate(requestDto.getPurchaseDate());
        existingHistory.setPurchasePrice(requestDto.getAmount());
        existingHistory.setVendorName(requestDto.getVendor());
        existingHistory.setInvoiceNumber(requestDto.getInvoiceNumber());
        existingHistory.setWarrantyPeriod(requestDto.getWarrantyPeriod());
        existingHistory.setExpiryDate(requestDto.getExpiryDate());
        existingHistory.setQty(requestDto.getQty());
        existingHistory.setNotify(requestDto.getNotify());
        existingHistory.setDescription(requestDto.getDescription());
        existingHistory.setLastChangeBy(user);
        if (file != null && !file.isEmpty()) {
            // Only accept PDF
            if (!"application/pdf".equals(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
            }
            try {
                // Delete old PDF from Cloudinary if exists
                if (existingHistory.getBillPublicId() != null) {
                    cloudinary.uploader().destroy(existingHistory.getBillPublicId(), ObjectUtils.asMap("resource_type", "raw"));
                }
                // Upload new PDF
                Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "bills/",
                        "public_id", "bill_" + id + "_" + System.currentTimeMillis(),
                        "format", "pdf",                              // optional, enforces .pdf extension
                        "type", "upload"
                    )
                );
                existingHistory.setBillUrl((String) uploadResult.get("secure_url"));
                existingHistory.setBillPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload bill PDF", e);
            }
        }
        PurchaseHistory updated = purchaseHistoryRepository.save(existingHistory);
        return purchaseHistoryMapper.toResponseDTO(updated);
    }

    // Optional: Keep this method if you still need it elsewhere
    private PurchaseHistoryResponseDTO toDTO(PurchaseHistory ph) {
        return purchaseHistoryMapper.toResponseDTO(ph);
    }
}