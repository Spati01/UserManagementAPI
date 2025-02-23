package com.NxtWave.Services;



import com.NxtWave.DTO.BulkUserUpdateRequestDTO;
import com.NxtWave.DTO.DeleteUserRequestDTO;
import com.NxtWave.DTO.UserRequestDTO;
import com.NxtWave.DTO.UserResponseDTO;
import com.NxtWave.DTO.UserUpdateResponseDTO;
import com.NxtWave.Entity.Manager;
import com.NxtWave.Entity.User;
import com.NxtWave.ExceptionHandling.UserNotFoundException;
import com.NxtWave.ExceptionHandling.ValidationException;
import com.NxtWave.Repository.ManagerRepository;
import com.NxtWave.Repository.UserRepository;
import com.NxtWave.Util.ValidationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepository userRepository;
    
    @Autowired
    private  ManagerRepository managerRepository;
    
    @Autowired
    private  ValidationUtil validationUtil;

     @PersistenceContext
    private EntityManager entityManager;

     private final TransactionTemplate transactionTemplate;

    @Override
    public void createUser(UserRequestDTO request) {
        log.info("Received request to create user: {}", request);
        validationUtil.validateCreateRequest(request);
    
        User user = new User();
        user.setFullName(request.getFullName());
        user.setMobNum(request.getMobNum());
        user.setPanNum(request.getPanNum());
    
        // Handle manager assignment
        if (request.getManagerId() != null) {
            Manager manager = managerRepository.findByManagerIdAndIsActive(request.getManagerId(), true)
                    .orElseThrow(() -> {
                        log.error("Manager not found or inactive: {}", request.getManagerId());
                        return new ValidationException("Invalid or inactive manager");
                    });
            user.setManager(manager); // ✅ Set the manager
        } else {
            log.warn("No Manager ID provided");
        }
    
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
    
        User savedUser = userRepository.save(user);
        log.info("Saved user ID: {}, Manager ID: {}", 
            savedUser.getUserId(), 
            (savedUser.getManager() != null) ? savedUser.getManager().getManagerId() : "None"
        );
    }
    // get all users, user by number and user by id and user by managerid 
    
       @Override
    public List<UserResponseDTO> getUsers(UserRequestDTO request) {
        List<User> users;

        if (request.getManagerId() != null) {
            // Get users by managerId
            users = userRepository.findByManager_ManagerId(request.getManagerId());
        } else if (request.getUserId() != null) {
            // Get user by userId (returns empty list if not found)
            users = userRepository.findByUserId(request.getUserId())
                    .map(List::of)
                    .orElseGet(ArrayList::new);
        } else if (request.getMobNum() != null) {
            // Get user by mobNum (returns empty list if not found)
            users = userRepository.findByMobNum(request.getMobNum())
                    .map(List::of)
                    .orElseGet(ArrayList::new);
        } else {
            // Get all users
            users = userRepository.findAll();
        }

        // Convert User entities to UserResponseDTO
        return users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());
    }




   
     
      @Override
      public void deleteUser(DeleteUserRequestDTO request) {
    validationUtil.validateDeleteRequest(request); 
    User user;

    if (request.getUserId() != null) {
        user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));
    } else {
        String normalizedMobNum = validationUtil.normalizeMobile(request.getMobNum());
        user = userRepository.findByMobNum(normalizedMobNum)
                .orElseThrow(() -> new UserNotFoundException("User not found with mobile: " + normalizedMobNum));
    }

    userRepository.delete(user);
}


@Override
public List<UserUpdateResponseDTO> updateUsers(BulkUserUpdateRequestDTO request) {
    // Request validation
    if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
        throw new ValidationException("At least one user_id must be provided");
    }
    if (request.getUpdateData() == null || request.getUpdateData().isEmpty()) {
        throw new ValidationException("Update data cannot be empty");
    }

    // Bulk update validation
    final boolean isBulkUpdate = request.getUserIds().size() > 1;
    final Map<String, Object> updateData = request.getUpdateData();
    
    if (isBulkUpdate) {
        Set<String> invalidKeys = updateData.keySet().stream()
                .filter(k -> !k.equals("manager_id"))
                .collect(Collectors.toSet());
        if (!invalidKeys.isEmpty()) {
            throw new ValidationException("Bulk updates can only modify manager_id. Invalid keys: " + invalidKeys);
        }
    }

    // Process updates
    List<UserUpdateResponseDTO> responses = new ArrayList<>();
    for (UUID userId : request.getUserIds()) {
        UserUpdateResponseDTO response = new UserUpdateResponseDTO();
        response.setUserId(userId);
        try {
            processUserUpdate(userId, updateData, isBulkUpdate, response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setUpdatedAt(null);
        }
        responses.add(response);
    }
    return responses;
}

private void processUserUpdate(UUID userId, Map<String, Object> updateData, 
                              boolean isBulkUpdate, UserUpdateResponseDTO response) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

    boolean newUserCreated = false;
    if (updateData.containsKey("manager_id")) {
        newUserCreated = handleManagerUpdate(user, updateData);
    }

    // Skip updates for deactivated users
    if (newUserCreated) {
        response.setSuccess(true);
        response.setMessage("Manager updated (new user created)");
        response.setUpdatedAt(LocalDateTime.now());
        return;
    }

    // Handle individual updates
    if (!isBulkUpdate) {
        updateUserFields(user, updateData);
    }

    // Update timestamp if needed
    if (user.getUpdatedAt() == null) {
        user.setUpdatedAt(LocalDateTime.now());
    }

    User savedUser = userRepository.saveAndFlush(user);
    response.setSuccess(true);
    response.setMessage("Updated successfully");
    response.setUpdatedAt(savedUser.getUpdatedAt());
}

private boolean handleManagerUpdate(User user, Map<String, Object> updateData) {
    UUID newManagerId = UUID.fromString(updateData.get("manager_id").toString());
    Manager newManager = managerRepository.findByManagerIdAndIsActive(newManagerId, true)
            .orElseThrow(() -> new ValidationException("Manager not found or inactive: " + newManagerId));

    if (user.getManager() != null) {
        // Deactivate old user in a separate transaction
        transactionTemplate.executeWithoutResult(status -> {
            user.setActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.saveAndFlush(user);
            entityManager.detach(user); // Remove from persistence context
        });

        // Create new user
        User newUser = new User();
        copyUserProperties(user, newUser);
        newUser.setUserId(UUID.randomUUID());
        newUser.setManager(newManager);
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now()); // Fresh timestamp
        userRepository.saveAndFlush(newUser);
        return true;
    } else {
        // Update existing user
        user.setManager(newManager);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.saveAndFlush(user);
        return false;
    }
}

private void copyUserProperties(User source, User target) {
    target.setFullName(source.getFullName());
    target.setMobNum(source.getMobNum());
    target.setPanNum(source.getPanNum());
    
}

private void updateUserFields(User user, Map<String, Object> updateData) {
    if (updateData.containsKey("full_name")) {
        user.setFullName((String) updateData.get("full_name"));
    }
    if (updateData.containsKey("mob_num")) {
        String mobNum = validationUtil.normalizeMobile(
            (String) updateData.get("mob_num")
        );
        validationUtil.validateMobileUpdate(user.getUserId(), mobNum);
        user.setMobNum(mobNum);
    }
    if (updateData.containsKey("pan_num")) {
        String panNum = ((String) updateData.get("pan_num")).toUpperCase();
        validationUtil.validatePanUpdate(user.getUserId(), panNum);
        user.setPanNum(panNum);
    }
}
}