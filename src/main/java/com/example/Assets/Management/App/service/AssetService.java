package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Asset;
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
import java.util.Optional;

import javax.management.RuntimeErrorException;

@Service
public class AssetService {
    private final AssetRepository assetRepository;

    private final AssetMapper assetMapper;

    @Autowired
    private Cloudinary cloudinary;

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

    public String uploadAssetImage(MultipartFile file){
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            // e.printStackTrace();
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
