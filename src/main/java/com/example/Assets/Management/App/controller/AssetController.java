package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.mapper.AssetMapper;
import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PaginatedResponse;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.service.AssetService;
import com.example.Assets.Management.App.dto.responseDto.ApiResponse;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/asset")
@Tag(name = "Asset", description = "Asset management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final AssetService assetService;
    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final ObjectMapper objectMapper; 
    private final UserRepository userRepository;


    public AssetController(AssetService assetService,AssetRepository assetRepository,AssetMapper assetMapper,ObjectMapper objectMapper, UserRepository userRepository) {
        this.assetService = assetService;
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get all assets")
    @GetMapping
    public ResponseEntity<?> getAllAssets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String status
    ) {
        PaginatedResponse<AssetResponseDTO> paginatedAssets = assetService.getAllAssets(page, limit, categoryId, status);

        ApiResponse<PaginatedResponse<AssetResponseDTO>> response =
            new ApiResponse<>(true, "Assets fetched successfully", paginatedAssets);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all assets")
    @GetMapping("/unpurchase-asset")
    public ResponseEntity<?> getAssetsNotInPurchaseHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String status
    ) {
        PaginatedResponse<AssetResponseDTO> paginatedAssets = assetService.getAssetsNotInPurchaseHistory(page, limit);

        ApiResponse<PaginatedResponse<AssetResponseDTO>> response =
            new ApiResponse<>(true, "Assets fetched successfully", paginatedAssets);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get asset by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @Operation(summary = "Create new asset")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAssetWithImage(
            @RequestPart("asset") String assetJson,
            @RequestPart(value = "file", required = false) MultipartFile file, Authentication authentication) {

        try {
            // Convert JSON string to DTO
            AssetRequestDTO assetRequestDTO = objectMapper.readValue(assetJson, AssetRequestDTO.class);

            // Convert DTO to entity
            Asset asset = assetMapper.toEntity(assetRequestDTO);
            System.out.println(asset);

            // If file is provided, upload image and set image data
            if (file != null && !file.isEmpty()) {
                Map<String, String> uploadResult = assetService.uploadAssetImage(file, asset);
                asset.setImageUrl(uploadResult.get("imageUrl"));
                asset.setImagePublicId(uploadResult.get("publicId"));
            }
            String changeby = authentication.getName();
            // Save the asset with or without image
            asset.setLastModifiedBy(userRepository.findByEmail(changeby).get());
            Asset savedAsset = assetRepository.save(asset);
            Map<String,Object> res = Map.of("data", assetMapper.toResponseDTO(savedAsset));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update asset by ID (with optional image upload)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAsset(
            @PathVariable Long id,
            @RequestPart("asset") String assetJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        try {
            AssetRequestDTO assetRequestDTO = objectMapper.readValue(assetJson, AssetRequestDTO.class);

            // Call your service to handle update logic
            Asset updatedAsset = assetService.updateAsset(id, assetRequestDTO);

            // Handle optional image upload
            if (file != null && !file.isEmpty()) {
                Map<String, String> uploadResult = assetService.uploadAssetImage(file, updatedAsset);
                updatedAsset.setImageUrl(uploadResult.get("imageUrl"));
                updatedAsset.setImagePublicId(uploadResult.get("publicId"));
            }

            // Update lastModifiedBy field
            String email = authentication.getName();
            updatedAsset.setLastModifiedBy(userRepository.findByEmail(email).get());

            // Save updated asset
            updatedAsset = assetRepository.save(updatedAsset);
            Map<String,Object> res = Map.of("data", assetMapper.toResponseDTO(updatedAsset));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Inactive asset by asset_id")
    @PutMapping("/inactive/{id}")
    public ResponseEntity<?> inactiveAsset(@PathVariable Long id) {
        assetService.inactiveAsset(id);
        return ResponseEntity.ok(Map.of("message", "Asset inactive successfully"));
    }

    @Operation(summary = "Inactive asset by asset_id")
    @PutMapping("/active/{id}")
    public ResponseEntity<?> activeAsset(@PathVariable Long id) {
        assetService.activeAsset(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete asset by asset_id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all accet by user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAssetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(assetService.getAssetsByUser(userId));
    }

    @Operation(summary = "Get all accet by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getAssetsByCategory(@PathVariable Long categoryId) {
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

    @Operation(summary = "Reassign asset to another user")
    @PutMapping("/{id}/reassign")
    public ResponseEntity<?> reassignAsset(
            @PathVariable Long id,
            @RequestParam Long userId,
            Authentication authentication) {
        String username = authentication.getName();
        AssetResponseDTO response = assetService.reassignAsset(id, userId, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Return asset (make available)")
    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnAsset(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        AssetResponseDTO response = assetService.returnAsset(id, username);
        return ResponseEntity.ok(response);
    }
}
