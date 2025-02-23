package com.NxtWave.DTO;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.NxtWave.Entity.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID userId;
    private UUID managerId;
    private String fullName;
    private String mobNum;
    private String panNum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;

    // Constructor to convert User to UserResponseDTO
    public UserResponseDTO(User user) {
        this.userId = user.getUserId();
        this.managerId = (user.getManager() != null) ? user.getManager().getManagerId() : null;
        this.fullName = user.getFullName();
        this.mobNum = user.getMobNum();
        this.panNum = user.getPanNum();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isActive = user.isActive();
    }
}