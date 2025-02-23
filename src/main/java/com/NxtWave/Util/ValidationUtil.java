package com.NxtWave.Util;

import com.NxtWave.Config.ValidationConfig;
import com.NxtWave.DTO.DeleteUserRequestDTO;
import com.NxtWave.DTO.UserRequestDTO;
import com.NxtWave.ExceptionHandling.ValidationException;
import com.NxtWave.Repository.ManagerRepository;
import com.NxtWave.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private final ValidationConfig validationConfig;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    public void isValidPan(String pan) {
        if (pan == null || pan.isEmpty()) {
            throw new ValidationException(validationConfig.getInvalidPanMessage());
        }

        if (!Pattern.compile(validationConfig.getPanNumberRegex()).matcher(pan).matches()) {
            throw new ValidationException(validationConfig.getInvalidPanMessage());
        }

        if (userRepository.existsByPanNum(pan)) {
            throw new ValidationException("PAN Card already exists. Please use a different PAN.");
        }
    }

    public String normalizeMobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            throw new ValidationException(validationConfig.getInvalidMobileMessage());
        }

        // Remove all non-digit characters
        String cleanedMobile = mobile.replaceAll("\\D", "");

        // Remove country code or leading zero
        if (cleanedMobile.startsWith("91") && cleanedMobile.length() > 10) {
            cleanedMobile = cleanedMobile.substring(2);
        } else if (cleanedMobile.startsWith("0") && cleanedMobile.length() > 10) {
            cleanedMobile = cleanedMobile.substring(1);
        }

        // Ensure final length is exactly 10 digits
        if (cleanedMobile.length() != 10) {
            throw new ValidationException("Mobile number must be exactly 10 digits.");
        }

        return cleanedMobile;
    }

    public void validateDuplicateMobile(String mobile) {
        String normalizedMobile = normalizeMobile(mobile);

        if (userRepository.existsByMobNum(normalizedMobile)) {
            throw new ValidationException("Mobile Number already exists. Please use a different number.");
        }
    }

    public void validateCreateRequest(UserRequestDTO request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new ValidationException(validationConfig.getFullNameRequiredMessage());
        }

        if (request.getManagerId() != null && !managerRepository.existsById(request.getManagerId())) {
            throw new ValidationException(validationConfig.getInvalidManagerMessage());
        }

        String normalizedMobile = normalizeMobile(request.getMobNum());
        validateDuplicateMobile(normalizedMobile); 
        isValidPan(request.getPanNum());

        // Update request object with the normalized mobile number
        request.setMobNum(normalizedMobile);
    }



    
    public void validateDeleteRequest(DeleteUserRequestDTO request) {
        if (request.getUserId() == null && request.getMobNum() == null) {
            throw new ValidationException("Either user_id or mob_num must be provided");
        }
    }

    public void validateBulkUpdate(Map<String, Object> updateData) {
        if (updateData.keySet().stream().anyMatch(k -> !k.equals("manager_id"))) {
            throw new ValidationException("Bulk updates only support manager_id changes");
        }
    }


    
    public void validateMobileUpdate(UUID userId, String mobile) {
        String normalized = normalizeMobile(mobile);
        userRepository.findByMobNum(normalized)
            .ifPresent(user -> {
                if (!user.getUserId().equals(userId)) {
                    throw new ValidationException("Mobile number already exists");
                }
            });
    }

    public void validatePanUpdate(UUID userId, String pan) {
        String normalizedPan = pan.toUpperCase();
        userRepository.findByPanNum(normalizedPan)
            .ifPresent(user -> {
                if (!user.getUserId().equals(userId)) {
                    throw new ValidationException("PAN already exists");
                }
            });
        isValidPan(normalizedPan);
    }
}
