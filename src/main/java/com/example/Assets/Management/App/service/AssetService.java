package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.AssetAssignmentHistory;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetAssignmentHistoryRepository;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.CategoryRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import com.example.Assets.Management.App.repository.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PaginatedResponse;
import com.cloudinary.Cloudinary;
import com.example.Assets.Management.App.dto.mapper.AssetMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;    

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AssetAssignmentHistoryRepository assignmentHistoryRepository;

    public PaginatedResponse<AssetResponseDTO> getAllAssets(int page, int size, Long categoryId, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asset> assets;
        Category category = null;
        if(categoryId != null){
            category = categoryRepository.findById(categoryId).orElse(null);
        }
        if (categoryId != null && status != null) {
            assets = assetRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (categoryId != null) {
            assets = assetRepository.findByCategory(category, pageable);
        } else if (status != null) {
            assets = assetRepository.findByStatus(status, pageable);
        } else {
            assets = assetRepository.findAll(pageable);
        }

        List<AssetResponseDTO> assetResponseDTOList = assets.getContent().stream()
                .map(assetMapper::toResponseDTO)
                .toList();

        return new PaginatedResponse<>(
            assetResponseDTOList,
            assets.getTotalElements(),
            assets.getNumber(),
            assets.getSize()
        );
    }

    public PaginatedResponse<AssetResponseDTO> getAssetsNotInPurchaseHistory(int page, int size) {
        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }
    
        // Create pageable request
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        
        // Get paginated results
        Page<Asset> assets = assetRepository.findAssetsNotInPurchaseHistory(pageable);
        
        // Convert to DTOs
        List<AssetResponseDTO> assetResponseDTOList = assets.stream()
                .map(assetMapper::toResponseDTO)
                .toList();
    
        // Return paginated response
        return new PaginatedResponse<>(
            assetResponseDTOList,
            assets.getTotalElements(),
            assets.getNumber(),
            assets.getSize()
        );
    }

    public List<AssetResponseDTO> getAllAssets() {
        List<AssetResponseDTO> assetResponseDTOList = assetRepository.findAll().stream().map(assetMapper::toResponseDTO).toList();
        return assetResponseDTOList;
    }

    public AssetResponseDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return assetMapper.toResponseDTO(asset);
    }

    public AssetResponseDTO createAsset(AssetRequestDTO assetRequestDTO) {
        Asset asset = assetMapper.toEntity(assetRequestDTO);
        Asset savedAsset = assetRepository.save(asset);
        return assetMapper.toResponseDTO(savedAsset);
    }

    public Asset updateAsset(Long id, AssetRequestDTO assetRequestDTO) {
        Asset asset = assetRepository.findById(id).get();
        asset.setCategory(categoryRepository.findById(assetRequestDTO.getCategoryId()).get());
        asset.setDescription(assetRequestDTO.getDescription());
        asset.setExpiryDate(assetRequestDTO.getExpiryDate());
        asset.setPurchaseDate(assetRequestDTO.getPurchaseDate());
        asset.setName(assetRequestDTO.getName());
        asset.setWarrantyPeriod(assetRequestDTO.getWarrantyPeriod());
        Asset updatedAsset = assetRepository.save(asset);
        return updatedAsset;
    }


    public void inactiveAsset(Long id){
        Asset asset = assetRepository.findById(id).get();
        asset.setStatus("INACTIVE");
        assetRepository.save(asset);
    }

    public void activeAsset(Long id){
        Asset asset = assetRepository.findById(id).get();
        asset.setStatus("AVAILABLE");
        assetRepository.save(asset);
    }

    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }

    public List<AssetResponseDTO> getAssetsByUser(Long userId) {
        Users user = userService.getUserById(userId);
        List<Asset> assets = assetRepository.findByAssignedToUser(user);
        return assets.stream()
                .map(assetMapper::toResponseDTO)
                .toList();
    }

    public List<AssetResponseDTO> getAssetsByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<Asset> assets = assetRepository.findByCategory(category);
        return assets.stream()
                .map(assetMapper::toResponseDTO)
                .toList();
    }

    public Map<String, String> uploadAssetImage(MultipartFile file, Asset asset) {
        try {
            // If the asset has an existing image, overwrite it
            Map<String, Object> uploadOptions = new HashMap<>();
            if (asset.getImagePublicId() != null) {
                uploadOptions.put("public_id", asset.getImagePublicId());
                uploadOptions.put("overwrite", true);
                uploadOptions.put("invalidate", true); // optional: invalidate CDN cache
            }

            // Upload image (will overwrite if public_id exists)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return Map.of(
                    "imageUrl", imageUrl,
                    "publicId", publicId);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    public AssetResponseDTO returnAsset(Long assetId, String modifiedBy) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        Users changeBy = userRepository.findByEmail(modifiedBy).orElseThrow();
        Users previousUser = asset.getAssignedToUser();
    
        asset.setAssignedToUser(null);
        asset.setStatus("AVAILABLE");
        asset.setLastModifiedBy(changeBy);
        assetRepository.save(asset);
    
        // Save return history
        if (previousUser != null) {
            AssetAssignmentHistory history = AssetAssignmentHistory.builder()
                .asset(asset)
                .assignedUser(previousUser)
                .changedBy(changeBy)
                .assignmentDate(LocalDateTime.now())
                .status("RETURNED")
                .build();
            assignmentHistoryRepository.save(history);
        }
    
        return assetMapper.toResponseDTO(asset);
    }

    public AssetResponseDTO reassignAsset(Long assetId, Long newUserId, String modifiedBy) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        Users newUser = userRepository.findById(newUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Users changeBy = userRepository.findByEmail(modifiedBy).orElseThrow();
    
        asset.setAssignedToUser(newUser);
        asset.setStatus("ASSIGNED");
        asset.setLastModifiedBy(changeBy);
        assetRepository.save(asset);
    
        // Save assignment history
        AssetAssignmentHistory history = AssetAssignmentHistory.builder()
            .asset(asset)
            .assignedUser(newUser)
            .changedBy(changeBy)
            .assignmentDate(LocalDateTime.now())
            .status("ASSIGNED")
            .build();
        assignmentHistoryRepository.save(history);
    
        return assetMapper.toResponseDTO(asset);
    }
    

}
