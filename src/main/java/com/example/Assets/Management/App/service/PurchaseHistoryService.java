package com.example.Assets.Management.App.service;

import com.example.Assets.Management.App.dto.mapper.PurchaseHistoryMapper;
import com.example.Assets.Management.App.dto.requestDto.PurchaseHistoryRequestDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryResponseDTO;
import com.example.Assets.Management.App.dto.responseDto.PurchaseHistoryPageResponse;
import com.example.Assets.Management.App.model.Asset;
import com.example.Assets.Management.App.model.PurchaseHistory;
import com.example.Assets.Management.App.model.Users;
import com.example.Assets.Management.App.repository.AssetRepository;
import com.example.Assets.Management.App.repository.PurchaseHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final AssetRepository assetRepository;
    private final PurchaseHistoryMapper purchaseHistoryMapper;
    private final Cloudinary cloudinary;
    private final DepreciationService depreciationService;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository, 
                                AssetRepository assetRepository,
                                PurchaseHistoryMapper purchaseHistoryMapper,
                                Cloudinary cloudinary,
                                DepreciationService depreciationService) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.assetRepository = assetRepository;
        this.purchaseHistoryMapper = purchaseHistoryMapper;
        this.cloudinary = cloudinary;
        this.depreciationService = depreciationService;
    }

    // public Page<PurchaseHistoryResponseDTO> getAll(int page, int size, String[] sort) {
    //     Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    //     Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
    //     return purchaseHistoryRepository.findAll(pageable)
    //             .map(purchaseHistoryMapper::toResponseDTO);
    // }

    public PurchaseHistoryPageResponse getAllWithTotalValue(int page, int size, String[] sort) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        Page<PurchaseHistory> purchaseHistoryPage = purchaseHistoryRepository.findAll(pageable);
        List<PurchaseHistoryResponseDTO> content = purchaseHistoryPage.getContent()
                .stream()
                .map(purchaseHistoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        double totalCurrentValue = content.stream()
                .mapToDouble(dto -> dto.getCurrentValue() != null ? dto.getCurrentValue() : 0.0)
                .sum();
        
        return PurchaseHistoryPageResponse.builder()
                .content(content)
                .pageNumber(purchaseHistoryPage.getNumber())
                .pageSize(purchaseHistoryPage.getSize())
                .totalElements(purchaseHistoryPage.getTotalElements())
                .totalPages(purchaseHistoryPage.getTotalPages())
                .totalCurrentValue(totalCurrentValue)
                .build();
    }

    // public Page<PurchaseHistoryResponseDTO> getByAssetId(Long assetId, int page, int size, String[] sort) {
    //     assetRepository.findById(assetId)
    //             .orElseThrow(() -> new EntityNotFoundException("Asset not found with ID: " + assetId));

    //     Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    //     Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        
    //     return purchaseHistoryRepository.findByAssetId(assetId, pageable)
    //             .map(purchaseHistoryMapper::toResponseDTO);
    // }

    public PurchaseHistoryPageResponse getByAssetIdWithTotalValue(Long assetId, int page, int size, String[] sort) {
        assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with ID: " + assetId));

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        Page<PurchaseHistory> purchaseHistoryPage = purchaseHistoryRepository.findByAssetId(assetId, pageable);
        List<PurchaseHistoryResponseDTO> content = purchaseHistoryPage.getContent()
                .stream()
                .map(purchaseHistoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        double totalCurrentValue = content.stream()
                .mapToDouble(dto -> dto.getCurrentValue() != null ? dto.getCurrentValue() : 0.0)
                .sum();
        
        return PurchaseHistoryPageResponse.builder()
                .content(content)
                .pageNumber(purchaseHistoryPage.getNumber())
                .pageSize(purchaseHistoryPage.getSize())
                .totalElements(purchaseHistoryPage.getTotalElements())
                .totalPages(purchaseHistoryPage.getTotalPages())
                .totalCurrentValue(totalCurrentValue)
                .build();
    }

    public PurchaseHistoryResponseDTO createWithBill(PurchaseHistoryRequestDTO requestDto, MultipartFile file, Users u) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        PurchaseHistory ph = purchaseHistoryMapper.fromRequestDTO(requestDto);
        ph.setAsset(asset);
        ph.setLastChangeBy(u);
        if (file != null && !file.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "bills/",
                        "public_id", "bill_" + System.currentTimeMillis(),
                        "format", "pdf",                              // optional, enforces .pdf extension
                        "type", "upload"
                    )
                );
                ph.setBillUrl((String) uploadResult.get("secure_url"));
                ph.setBillPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload bill PDF", e);
            }
        }
        PurchaseHistory saved = purchaseHistoryRepository.save(ph);
        return purchaseHistoryMapper.toResponseDTO(saved);
    }

    public void delete(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    public PurchaseHistoryResponseDTO getById(Long id) {
        PurchaseHistory purchaseHistory = purchaseHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));
        return purchaseHistoryMapper.toResponseDTO(purchaseHistory);
    }

    public PurchaseHistoryResponseDTO updateWithBill(Long id, PurchaseHistoryRequestDTO requestDto, MultipartFile file, Users user) {
        PurchaseHistory existingHistory = purchaseHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase History not found with ID: " + id));
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asset not found"));
        existingHistory.setAsset(asset);
        existingHistory.setPurchaseDate(requestDto.getPurchaseDate());
        existingHistory.setPurchasePrice(requestDto.getAmount());
        existingHistory.setVendorName(requestDto.getVendor());
        existingHistory.setInvoiceNumber(requestDto.getInvoiceNumber());
        existingHistory.setWarrantyPeriod(requestDto.getWarrantyPeriod());
        existingHistory.setExpiryDate(requestDto.getExpiryDate());
        existingHistory.setQty(requestDto.getQty());
        existingHistory.setNotify(requestDto.getNotify());
        existingHistory.setDescription(requestDto.getDescription());
        existingHistory.setLastChangeBy(user);
        if (file != null && !file.isEmpty()) {
            // Only accept PDF
            if (!"application/pdf".equals(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
            }
            try {
                // Delete old PDF from Cloudinary if exists
                if (existingHistory.getBillPublicId() != null) {
                    cloudinary.uploader().destroy(existingHistory.getBillPublicId(), ObjectUtils.asMap("resource_type", "raw"));
                }
                // Upload new PDF
                Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "bills/",
                        "public_id", "bill_" + id + "_" + System.currentTimeMillis(),
                        "format", "pdf",                              // optional, enforces .pdf extension
                        "type", "upload"
                    )
                );
                existingHistory.setBillUrl((String) uploadResult.get("secure_url"));
                existingHistory.setBillPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload bill PDF", e);
            }
        }
        PurchaseHistory updated = purchaseHistoryRepository.save(existingHistory);
        return purchaseHistoryMapper.toResponseDTO(updated);
    }

    public byte[] exportPurchaseHistoryToExcel(Long assetId) throws IOException {
        List<PurchaseHistory> histories = (assetId != null)
                ? purchaseHistoryRepository.findByAssetId(assetId)
                : purchaseHistoryRepository.findAll();

        String[] columns = {
            "S.No", "Asset Name", "Category Name", "Purchase Date", "Amount", "Vendor",
            "Invoice", "Expiry Date", "Qty", "Current Value", "Depreciation Value"
        };

        double totalPurchasePrice = 0.0;
        double totalCurrentValue = 0.0;

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Purchase History");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // Decimal style
            CellStyle decimalStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            decimalStyle.setDataFormat(format.getFormat("0.00"));

            // Summary style
            CellStyle summaryStyle = workbook.createCellStyle();
            summaryStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);

            // Header row
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            int serialNumber = 1;
            for (PurchaseHistory ph : histories) {
                Row row = sheet.createRow(rowIdx++);
                Asset asset = ph.getAsset();
                String categoryName = asset.getCategory() != null ? asset.getCategory().getName() : "";

                row.createCell(0).setCellValue(serialNumber++);
                row.createCell(1).setCellValue(asset.getName());
                row.createCell(2).setCellValue(categoryName);
                row.createCell(3).setCellValue(ph.getPurchaseDate().toString());

                Cell amountCell = row.createCell(4);
                amountCell.setCellValue(roundTo2Decimal(ph.getPurchasePrice()));
                amountCell.setCellStyle(decimalStyle);

                row.createCell(5).setCellValue(ph.getVendorName());
                row.createCell(6).setCellValue(ph.getInvoiceNumber());
                row.createCell(7).setCellValue(ph.getExpiryDate().toString());
                row.createCell(8).setCellValue(ph.getQty());

                // Calculate current value and depreciation value
                double currentValue = 0.0;
                double depreciationValue = 0.0;
                try {
                    currentValue = depreciationService.getCurrentValue(
                        ph.getPurchasePrice(),
                        ph.getPurchaseDate(),
                        asset.getCategory().getId(),
                        java.time.LocalDate.now()
                    );
                    depreciationValue = ph.getPurchasePrice() - currentValue;
                } catch (Exception e) {
                    // Optionally log: logger.warn("Depreciation calculation failed for assetId: " + asset.getId(), e);
                }
                Cell currentValueCell = row.createCell(9);
                currentValueCell.setCellValue(roundTo2Decimal(currentValue));
                currentValueCell.setCellStyle(decimalStyle);

                Cell depreciationCell = row.createCell(10);
                depreciationCell.setCellValue(roundTo2Decimal(depreciationValue));
                depreciationCell.setCellStyle(decimalStyle);

                totalPurchasePrice += ph.getPurchasePrice();
                totalCurrentValue += currentValue;
            }

            // Add summary row
            Row summaryRow = sheet.createRow(rowIdx);
            summaryRow.createCell(3).setCellValue("TOTAL:");
            Cell totalPurchaseCell = summaryRow.createCell(4);
            totalPurchaseCell.setCellValue(roundTo2Decimal(totalPurchasePrice));
            totalPurchaseCell.setCellStyle(summaryStyle);
            Cell totalCurrentCell = summaryRow.createCell(9);
            totalCurrentCell.setCellValue(roundTo2Decimal(totalCurrentValue));
            totalCurrentCell.setCellStyle(summaryStyle);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private double roundTo2Decimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}