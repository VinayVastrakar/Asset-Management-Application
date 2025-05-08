package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.service.PurchaseHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-history")
@Tag(name = "Purchase History", description = "Purchase History APIs")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseHistoryController {
    private final PurchaseHistoryService service;

    public PurchaseHistoryController(PurchaseHistoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Purchase History")
    public List<PurchaseHistoryResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/asset/{assetId}")
    @Operation(summary = "Get Purchase History by Asset")
    public List<PurchaseHistoryResponseDTO> getByAssetId(@PathVariable Long assetId) {
        return service.getByAssetId(assetId);
    }

    @PostMapping
    @Operation(summary = "Add Purchase History")
    public PurchaseHistoryResponseDTO create(@RequestBody PurchaseHistoryRequestDTO dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove Purchase History")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
