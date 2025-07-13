package com.example.Assets.Management.App.dto.requestDto;

import com.example.Assets.Management.App.Enums.AssetStatus;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssetRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
    private Integer warrantyPeriod; // in months
    private AssetStatus status;
    private LocalDateTime stolenDate;
    private String stolenReportedBy;
    private String stolenNotes;
}
