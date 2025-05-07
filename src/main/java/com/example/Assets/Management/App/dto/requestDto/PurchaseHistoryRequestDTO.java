package com.example.Assets.Management.App.dto.requestDto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseHistoryRequestDTO {
    private Long assetId;
    private LocalDate purchaseDate;
    private Double amount;
    private String vendor;
}
