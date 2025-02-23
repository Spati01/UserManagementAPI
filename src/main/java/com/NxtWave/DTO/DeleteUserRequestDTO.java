package com.NxtWave.DTO;


import lombok.Data;
import java.util.UUID;

@Data
public class DeleteUserRequestDTO {
    private UUID userId;
    private String mobNum;
}