package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.cloudinary.Cloudinary;
import com.example.Assets.Management.App.dto.mapper.AssetMapper;

import java.util.List;
import java.util.Map;


@Service
public class AssetService {
    private final AssetRepository assetRepository;

    private final AssetMapper assetMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();   
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

    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO assetRequestDTO) {
        Asset asset = assetMapper.toEntity(assetRequestDTO);
        asset.setId(id);
        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponseDTO(updatedAsset);
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
            // Delete old image if it exists
            if (asset.getImagePublicId() != null) {
                cloudinary.uploader().destroy(asset.getImagePublicId(), Map.of());
            }

            // Upload new image
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            // Return both values in a map
            return Map.of(
                    "imageUrl", imageUrl,
                    "publicId", publicId
            );

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

}
