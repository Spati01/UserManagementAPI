package com.NxtWave.Controllers;


import com.NxtWave.DTO.BulkUserUpdateRequestDTO;
import com.NxtWave.DTO.DeleteUserRequestDTO;
import com.NxtWave.DTO.UserRequestDTO;
import com.NxtWave.DTO.UserResponseDTO;
import com.NxtWave.DTO.UserUpdateResponseDTO;
import com.NxtWave.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class userController {

    @Autowired
    private UserService userService;


    @GetMapping("/home")
        public String getHome() {
            return "Welcome to the User Home Page Currently Loading";
        }




    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO request) {
        userService.createUser(request);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }

    @PostMapping("/get_users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestBody UserRequestDTO request) {
        List<UserResponseDTO> users = userService.getUsers(request);
        return ResponseEntity.ok(Map.of("users", users));
    }


    @PostMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRequestDTO request) {
        if (request.getUserId() == null && request.getMobNum() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please provide either user_id or mob_num"));
        }

        userService.deleteUser(request);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PostMapping("/update_user")
    public ResponseEntity<?> updateUser(@RequestBody BulkUserUpdateRequestDTO request) {
        List<UserUpdateResponseDTO> responses = userService.updateUsers(request);
        return ResponseEntity.ok(Map.of("results", responses));
    }

}
