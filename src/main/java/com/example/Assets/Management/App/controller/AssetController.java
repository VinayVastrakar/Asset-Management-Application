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
    public ResponseEntity getAllAssets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            PaginatedResponse<AssetResponseDTO> paginatedAssets = assetService.getAllAssets(page, size);

    ApiResponse<PaginatedResponse<AssetResponseDTO>> response =
        new ApiResponse<>(true, "Assets fetched successfully", paginatedAssets);

    return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get asset by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable Long id) {
        return new ResponseEntity<>(assetService.getAssetById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create new asset")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssetResponseDTO> createAssetWithImage(
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

            return ResponseEntity.ok(assetMapper.toResponseDTO(savedAsset));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

    @Operation(summary = "Reassign asset to another user")
    @PutMapping("/{id}/reassign")
    public ResponseEntity<AssetResponseDTO> reassignAsset(
            @PathVariable Long id,
            @RequestParam Long newUserId,
            Authentication authentication) {
        String username = authentication.getName();
        AssetResponseDTO response = assetService.reassignAsset(id, newUserId, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Return asset (make available)")
    @PutMapping("/{id}/return")
    public ResponseEntity<AssetResponseDTO> returnAsset(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        AssetResponseDTO response = assetService.returnAsset(id, username);
        return ResponseEntity.ok(response);
    }
}
