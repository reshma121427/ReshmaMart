package com.reshmamart.service;

import com.reshmamart.dto.LoginRequestDTO;
import com.reshmamart.dto.RegisterRequestDTO;
import com.reshmamart.dto.UserResponseDTO;
import com.reshmamart.model.User;
import java.util.List;

public interface UserService {
    UserResponseDTO register(RegisterRequestDTO request);
    User login(LoginRequestDTO request);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    int getTotalUserCount();
}
