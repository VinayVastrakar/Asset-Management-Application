package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryPageResponse;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.service.PurchaseHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/purchase-history")
@Tag(name = "Purchase History", description = "Purchase History APIs")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseHistoryController {
    private final PurchaseHistoryService service;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public PurchaseHistoryController(PurchaseHistoryService service, UserRepository userRepository,ObjectMapper objectMapper) {
        this.service = service;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @Operation(summary = "Get Purchase History with optional asset filter and pagination")
    public PurchaseHistoryPageResponse getPurchaseHistories(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Sorting criteria in the format: property,direction", 
                example = "purchaseDate,desc")
        @RequestParam(defaultValue = "purchaseDate,desc") String[] sort,

        @Parameter(description = "Optional asset ID to filter by")
        @RequestParam(required = false) Long assetId
    ) {
        if (assetId != null) {
            return service.getByAssetIdWithTotalValue(assetId, page, size, sort);
        } else {
            return service.getAllWithTotalValue(page, size, sort);
        }
    }    

    


    @GetMapping("/{id}")
    @Operation(summary = "Get Purchase History by ID")
    public PurchaseHistoryResponseDTO getById(
        @Parameter(description = "ID of the purchase history")
        @PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add Purchase History with optional bill PDF")
    public ResponseEntity<?> createPurchaseHistoryWithBill(
            @RequestPart("purchaseHistory") String purchaseHistoryJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) {
        try {
            // Parse JSON to DTO
            PurchaseHistoryRequestDTO dto = objectMapper.readValue(purchaseHistoryJson, PurchaseHistoryRequestDTO.class);
            String user = authentication.getName();
            Users u = userRepository.findByEmail(user)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Call service to handle creation and file upload
            PurchaseHistoryResponseDTO response = service.createWithBill(dto, file, u);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Purchase History")
    public ResponseEntity<?> update(
        @Parameter(description = "ID of the purchase history to update")
        @PathVariable Long id,
        @RequestPart("purchaseHistory") String purchaseHistoryJson,
        @RequestPart(value = "file", required = false) MultipartFile file,
        Authentication authentication) {
        String user = authentication.getName();
        Users u = userRepository.findByEmail(user)
            .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            PurchaseHistoryRequestDTO dto = objectMapper.readValue(purchaseHistoryJson, PurchaseHistoryRequestDTO.class);
            return ResponseEntity.ok(service.updateWithBill(id, dto, file, u));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse request or update purchase history", e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove Purchase History")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID of the purchase history to delete")
        @PathVariable Long id) {
        
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}