package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.repository.AssetRepository;
import org.springframework.stereotype.Service;
import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.dto.mapper.AssetMapper;

import java.util.List;
import java.util.Optional;

@Service
public class AssetService {
    private final AssetRepository assetRepository;

    private final AssetMapper assetMapper;

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
}
