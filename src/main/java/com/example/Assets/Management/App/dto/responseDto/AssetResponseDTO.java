package com.example.Assets.Management.App.dto.responseDto;

import com.example.Assets.Management.App.Enums.AssetStatus;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private String notify;
    private Integer warrantyPeriod; // in months
    private AssetStatus status;
    private LocalDateTime stolenDate;
    private String stolenReportedBy;
    private String stolenNotes;
    private String disposedNotes;
    private String assignedToUserName;
}

