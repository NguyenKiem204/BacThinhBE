package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.UserCreationRequest;
import com.bacthinh.BacThinh.dto.request.UserSelfUpdateRequest;
import com.bacthinh.BacThinh.dto.request.UserUpdateRequest;
import com.bacthinh.BacThinh.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService{
    UserResponse createUser(UserCreationRequest userCreationRequest);
    UserResponse updateByAdmin(Long userId, UserUpdateRequest userUpdateRequest);
    UserResponse updateSelf(Long userId, UserSelfUpdateRequest userUpdateRequest);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> searchUsers(String keyword, Pageable pageable);
    void deleteUser(Long id);
    UserDetails loadUserByEmail(String email);
}