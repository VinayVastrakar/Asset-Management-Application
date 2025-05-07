package com.example.Assets.Management.App.dto.responseDto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseHistoryResponseDTO {
    private Long id;
    private Long assetId;
    private String assetName;
    private LocalDate purchaseDate;
    private Double amount;
    private String vendor;
}
