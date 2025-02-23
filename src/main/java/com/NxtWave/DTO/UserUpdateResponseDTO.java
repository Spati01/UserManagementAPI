package com.NxtWave.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateResponseDTO {
    private UUID userId;
    private boolean success;
    private String message;
    private LocalDateTime updatedAt;
}