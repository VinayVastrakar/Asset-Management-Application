package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.service.PurchaseHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-history")
@Tag(name = "Purchase History", description = "Purchase History APIs")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseHistoryController {
    private final PurchaseHistoryService service;
    private final UserRepository userRepository;

    public PurchaseHistoryController(PurchaseHistoryService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all Purchase History with pagination")
    public Page<PurchaseHistoryResponseDTO> getAll(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") int size,
        
        @Parameter(description = "Sorting criteria in the format: property,direction", 
                   example = "purchaseDate,desc")
        @RequestParam(defaultValue = "purchaseDate,desc") String[] sort) {
        
        return service.getAll(page, size, sort);
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get Purchase History by Asset with pagination")
    public Page<PurchaseHistoryResponseDTO> getByAssetId(
        @Parameter(description = "ID of the asset") 
        @PathVariable Long assetId,
        
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") int size,
        
        @Parameter(description = "Sorting criteria in the format: property,direction", 
                   example = "purchaseDate,desc")
        @RequestParam(defaultValue = "purchaseDate,desc") String[] sort) {
        
        return service.getByAssetId(assetId, page, size, sort);
    }

    @PostMapping
    @Operation(summary = "Add Purchase History")
    public PurchaseHistoryResponseDTO create(
        @RequestBody PurchaseHistoryRequestDTO dto, 
        Authentication authentication) {
        
        String user = authentication.getName(); 
        Users u = userRepository.findByEmail(user)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return service.create(dto, u);
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