package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.mapper.AssetMapper;
import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.service.AssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Asset", description = "Asset management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final AssetService assetService;
    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    public AssetController(AssetService assetService,AssetRepository assetRepository,AssetMapper assetMapper) {
        this.assetService = assetService;
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    @Operation(summary = "Get all assets")
    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    @Operation(summary = "Get asset by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable Long id) {
        return new ResponseEntity<>(assetService.getAssetById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create new asset")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssetResponseDTO> createAssetWithImage(
            @RequestPart("asset") AssetRequestDTO assetRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // Convert DTO to entity
        Asset asset = assetMapper.toEntity(assetRequestDTO);

        // If file is provided, upload image and set image data
        if (file != null && !file.isEmpty()) {
            Map<String, String> uploadResult = assetService.uploadAssetImage(file, asset);
            asset.setImageUrl(uploadResult.get("imageUrl"));
            asset.setImagePublicId(uploadResult.get("publicId"));
        }

        // Save the asset with or without image
        Asset savedAsset = assetRepository.save(asset);

        return ResponseEntity.ok(assetMapper.toResponseDTO(savedAsset));
    }

    @Operation(summary = "Update asset by asset_id")
    @PutMapping("/{id}")
    public AssetResponseDTO updateAsset(@PathVariable Long id, @RequestBody AssetRequestDTO assetRequestDTO) {
        return assetService.updateAsset(id, assetRequestDTO);
    }

    @Operation(summary = "Delete asset by asset_id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all accet by user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AssetResponseDTO>> getAssetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(assetService.getAssetsByUser(userId));
    }

    @Operation(summary = "Get all accet by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AssetResponseDTO>> getAssetsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(assetService.getAssetsByCategory(categoryId));
    }

    @Operation(summary = "Upload asset image")
    @PutMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadAssetImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));

        // Upload image and delete old one
        Map<String, String> uploadResult = assetService.uploadAssetImage(file, asset);

        // Update asset with new image URL and public ID
        asset.setImageUrl(uploadResult.get("imageUrl"));
        asset.setImagePublicId(uploadResult.get("publicId"));
        assetRepository.save(asset);

        return ResponseEntity.ok(Map.of("imageUrl", uploadResult.get("imageUrl")));
    }
}
