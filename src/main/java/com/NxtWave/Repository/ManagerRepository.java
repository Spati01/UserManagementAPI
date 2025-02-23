package com.NxtWave.Repository;


import com.NxtWave.Entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    public Optional<Manager> findByManagerIdAndIsActive(UUID managerId, boolean isActive);
    

}
