package com.NxtWave.DTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BulkUserUpdateRequestDTO {
    @JsonProperty("user_ids") 
    private List<UUID> userIds;

    @JsonProperty("update_data") 
    private Map<String, Object> updateData;
}

