package com.NxtWave.Services;

import java.util.List;
import java.util.Map;

import com.NxtWave.DTO.BulkUserUpdateRequestDTO;
import com.NxtWave.DTO.DeleteUserRequestDTO;
import com.NxtWave.DTO.UserRequestDTO;
import com.NxtWave.DTO.UserResponseDTO;
import com.NxtWave.DTO.UserUpdateResponseDTO;


public interface UserService {

    void createUser(UserRequestDTO userRequestDTO);

    List<UserResponseDTO> getUsers(UserRequestDTO request);

    void deleteUser(DeleteUserRequestDTO request);
    
  List<UserUpdateResponseDTO> updateUsers(BulkUserUpdateRequestDTO request);
}
