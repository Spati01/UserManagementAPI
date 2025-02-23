package com.NxtWave.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Manager {
    
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR) 
    @Column(name = "manager_id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false, unique = true)
    private UUID managerId;

    
    
    @Column(nullable = false)
    private String name;
     
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; 
    
}
