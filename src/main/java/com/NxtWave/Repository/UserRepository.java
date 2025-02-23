package com.NxtWave.Repository;


import com.NxtWave.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserId(UUID userId);
    
    //List<User> findByMobNum(String mobile);
    Optional<User> findByMobNum(String mobile);

    List<User> findByManager_ManagerId(UUID managerId);


    boolean existsByPanNum(String panNum);  
    boolean existsByMobNum(String mobNum);

    Optional<User> findByPanNum(String normalizedPan);  

    Optional<User> findByUserIdAndIsActive(UUID userId, boolean isActive);


}
