package com.NxtWave.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data 
@NoArgsConstructor  
@AllArgsConstructor  
public class UserRequestDTO {
    
    private UUID userId;
    private String fullName;
    private String mobNum;
    private String panNum;

    @JsonProperty("manager_id")
    private UUID managerId;
}
