package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.AssetAssignmentHistory;
import com.example.Assets.Management.App.model.Category;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetAssignmentHistoryRepository;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.CategoryRepository;
import com.example.Assets.Management.App.repository.UserRepository;
import com.example.Assets.Management.App.Enums.AssetStatus;

import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Assets.Management.App.dto.requestDto.AssetRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.AssetResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PaginatedResponse;
import com.cloudinary.Cloudinary;
import com.example.Assets.Management.App.dto.mapper.AssetMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssetService {
    
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AssetAssignmentHistoryRepository assignmentHistoryRepository;

    @Autowired
    private DepreciationService depreciationService;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    public PaginatedResponse<AssetResponseDTO> getAllAssets(int page, int size, Long categoryId, AssetStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asset> assets;
        Category category = null;
        if(categoryId != null){
            category = categoryRepository.findById(categoryId).orElse(null);
        }
        if (categoryId != null && status != null) {
            assets = assetRepository.findByCategoryAndStatus(category, status, pageable);
        } else if (categoryId != null) {
            assets = assetRepository.findByCategory(category, pageable);
        } else if (status != null) {
            assets = assetRepository.findByStatus(status, pageable);
        } else {
            assets = assetRepository.findAll(pageable);
        }

        List<AssetResponseDTO> assetResponseDTOList = assets.getContent().stream()
                .map(assetMapper::toResponseDTO)
                .toList();

        return new PaginatedResponse<>(
            assetResponseDTOList,
            assets.getTotalElements(),
            assets.getNumber(),
            assets.getSize()
        );
    }

    public PaginatedResponse<AssetResponseDTO> getAssetsNotInPurchaseHistory(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }
    
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Asset> assets = assetRepository.findAssetsNotInPurchaseHistory(pageable);
        List<AssetResponseDTO> assetResponseDTOList = assets.stream()
                .map(assetMapper::toResponseDTO)
                .toList();
    
        return new PaginatedResponse<>(
            assetResponseDTOList,
            assets.getTotalElements(),
            assets.getNumber(),
            assets.getSize()
        );
    }

    public List<AssetResponseDTO> getAllAssets() {
        List<AssetResponseDTO> assetResponseDTOList = assetRepository.findAll().stream().map(assetMapper::toResponseDTO).toList();
        return assetResponseDTOList;
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

    public AssetResponseDTO createAssetWithImage(AssetRequestDTO assetRequestDTO, MultipartFile file, String modifiedBy) {
        Asset asset = assetMapper.toEntity(assetRequestDTO);
        if (file != null && !file.isEmpty()) {
            Map<String, String> uploadResult = uploadAssetImage(file, asset);
            asset.setImageUrl(uploadResult.get("imageUrl"));
            asset.setImagePublicId(uploadResult.get("publicId"));
        }
        asset.setLastModifiedBy(userRepository.findByEmail(modifiedBy).get());
        Asset savedAsset = assetRepository.save(asset);
        return assetMapper.toResponseDTO(savedAsset);
    }

    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO assetRequestDTO) {
        Asset asset = assetRepository.findById(id).get();
        asset.setCategory(categoryRepository.findById(assetRequestDTO.getCategoryId()).get());
        asset.setDescription(assetRequestDTO.getDescription());
        asset.setName(assetRequestDTO.getName());
        asset.setWarrantyPeriod(assetRequestDTO.getWarrantyPeriod());
        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponseDTO(updatedAsset);
    }

    public AssetResponseDTO updateAssetWithImage(Long id, AssetRequestDTO assetRequestDTO, MultipartFile file, String modifiedBy) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setCategory(categoryRepository.findById(assetRequestDTO.getCategoryId()).get());
        asset.setDescription(assetRequestDTO.getDescription());
        asset.setName(assetRequestDTO.getName());
        asset.setWarrantyPeriod(assetRequestDTO.getWarrantyPeriod());
        if (file != null && !file.isEmpty()) {
            Map<String, String> uploadResult = uploadAssetImage(file, asset);
            asset.setImageUrl(uploadResult.get("imageUrl"));
            asset.setImagePublicId(uploadResult.get("publicId"));
        }
        asset.setLastModifiedBy(userRepository.findByEmail(modifiedBy).get());
        Asset updatedAsset = assetRepository.save(asset);
        return assetMapper.toResponseDTO(updatedAsset);
    }

    public Map<String, String> updateAssetImage(Long id, MultipartFile file) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        Map<String, String> uploadResult = uploadAssetImage(file, asset);
        asset.setImageUrl(uploadResult.get("imageUrl"));
        asset.setImagePublicId(uploadResult.get("publicId"));
        assetRepository.save(asset);
        return uploadResult;
    }

    public void inactiveAsset(Long id){
        Asset asset = assetRepository.findById(id).get();
        asset.setStatus(AssetStatus.INACTIVE);
        assetRepository.save(asset);
    }

    public void activeAsset(Long id){
        Asset asset = assetRepository.findById(id).get();
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);
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
            Map<String, Object> uploadOptions = new HashMap<>();
            if (asset.getImagePublicId() != null) {
                uploadOptions.put("public_id", asset.getImagePublicId());
                uploadOptions.put("overwrite", true);
                uploadOptions.put("invalidate", true);
            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();
            return Map.of(
                    "imageUrl", imageUrl,
                    "publicId", publicId);
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    public AssetResponseDTO returnAsset(Long assetId, String modifiedBy) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        Users changeBy = userRepository.findByEmail(modifiedBy).orElseThrow();
        Users previousUser = asset.getAssignedToUser();
    
        asset.setAssignedToUser(null);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setLastModifiedBy(changeBy);
        assetRepository.save(asset);
    
        if (previousUser != null) {
            AssetAssignmentHistory history = AssetAssignmentHistory.builder()
                .asset(asset)
                .assignedUser(previousUser)
                .changedBy(changeBy)
                .assignmentDate(LocalDateTime.now())
                .status("RETURNED")
                .build();
            assignmentHistoryRepository.save(history);
        }
    
        return assetMapper.toResponseDTO(asset);
    }

    public AssetResponseDTO reassignAsset(Long assetId, Long newUserId, String modifiedBy) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        Users newUser = userRepository.findById(newUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Users changeBy = userRepository.findByEmail(modifiedBy).orElseThrow();
    
        asset.setAssignedToUser(newUser);
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setLastModifiedBy(changeBy);
        assetRepository.save(asset);
    
        AssetAssignmentHistory history = AssetAssignmentHistory.builder()
            .asset(asset)
            .assignedUser(newUser)
            .changedBy(changeBy)
            .assignmentDate(LocalDateTime.now())
            .status("ASSIGNED")
            .build();
        assignmentHistoryRepository.save(history);
    
        return assetMapper.toResponseDTO(asset);
    }
    

    public byte[] exportAssetAssignmentHistoryToExcel(Long assetId, Long categoryId) throws IOException {
        List<AssetAssignmentHistory> histories;
        
        if (assetId != null && categoryId != null) {
            histories = assignmentHistoryRepository.findByAssetIdAndCategoryId(assetId, categoryId);
        } else if (assetId != null) {
            histories = assignmentHistoryRepository.findByAssetId(assetId);
        } else if (categoryId != null) {
            histories = assignmentHistoryRepository.findByCategoryId(categoryId);
        } else {
            histories = assignmentHistoryRepository.findAll();
        }

        String[] columns = {
            "S.No", "Asset Name", "Category", "Assigned User", "Status", "Assignment Date"
        };

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Asset Assignment History");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            CellStyle dateStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy hh:mm"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            int serialNumber = 1;
            for (AssetAssignmentHistory history : histories) {
                Row row = sheet.createRow(rowIdx++);
                Asset asset = history.getAsset();
                String categoryName = asset.getCategory() != null ? asset.getCategory().getName() : "";
                String assignedUserName = history.getAssignedUser() != null ? history.getAssignedUser().getName() : "";

                row.createCell(0).setCellValue(serialNumber++);
                row.createCell(1).setCellValue(asset.getName());
                row.createCell(2).setCellValue(categoryName);
                row.createCell(3).setCellValue(assignedUserName);
                row.createCell(4).setCellValue(history.getStatus());

                Cell dateCell = row.createCell(5);
                dateCell.setCellValue(history.getAssignmentDate());
                dateCell.setCellStyle(dateStyle);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional
    public void markAssetAsStolen(Long assetId, String reportedBy, String notes) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        if (asset.getStatus() == AssetStatus.DISPOSED) {
            throw new IllegalStateException("Cannot mark as stolen: asset is already disposed.");
        }
        List<PurchaseHistory> histories = purchaseHistoryRepository.findByAssetId(assetId);

        if (histories != null && !histories.isEmpty()) {
            for (PurchaseHistory ph : histories) {
                double currentValue = depreciationService.getCurrentValue(
                    ph.getPurchasePrice(),
                    ph.getPurchaseDate(),
                    asset.getCategory().getId(),
                    LocalDate.now()
                );
                ph.setStolenValue(currentValue);
                purchaseHistoryRepository.save(ph);
            }
        }

        asset.setStolenDate(LocalDateTime.now());
        asset.setStatus(AssetStatus.STOLEN);
        asset.setStolenReportedBy(reportedBy);
        asset.setStolenNotes(notes);
        assetRepository.save(asset);
    }

    @Transactional
    public void markAssetAsDisposed(Long assetId, String disposedBy, String notes) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        if (asset.getStatus() == AssetStatus.STOLEN) {
            throw new IllegalStateException("Cannot mark as disposed: asset is already stolen.");
        }
        List<PurchaseHistory> histories = purchaseHistoryRepository.findByAssetId(assetId);

        if (histories != null && !histories.isEmpty()) {
            for (PurchaseHistory ph : histories) {
                double currentValue = depreciationService.getCurrentValue(
                    ph.getPurchasePrice(),
                    ph.getPurchaseDate(),
                    asset.getCategory().getId(),
                    LocalDate.now()
                );
                ph.setDisposedValue(currentValue);
                purchaseHistoryRepository.save(ph);
            }
        }
        asset.setDisposedDate(LocalDateTime.now());
        asset.setDisposedNotes(notes);
        asset.setStatus(AssetStatus.DISPOSED);
        assetRepository.save(asset);
    }
}
