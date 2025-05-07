package com.example.Assets.Management.App.controller;

import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.service.PurchaseHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-history")
public class PurchaseHistoryController {
    private final PurchaseHistoryService service;

    public PurchaseHistoryController(PurchaseHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseHistoryResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/asset/{assetId}")
    public List<PurchaseHistoryResponseDTO> getByAssetId(@PathVariable Long assetId) {
        return service.getByAssetId(assetId);
    }

    @PostMapping
    public PurchaseHistoryResponseDTO create(@RequestBody PurchaseHistoryRequestDTO dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
