package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.UserCreationRequest;
import com.bacthinh.BacThinh.dto.request.UserSelfUpdateRequest;
import com.bacthinh.BacThinh.dto.request.UserUpdateRequest;
import com.bacthinh.BacThinh.dto.response.UserResponse;
import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.entity.UserStatus;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.exception.UserExistException;
import com.bacthinh.BacThinh.mapper.UserMapper;
import com.bacthinh.BacThinh.repository.UserRepository;
import com.bacthinh.BacThinh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserExistException("Email already exists!");
        }
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.GIAO_DAN);
        user.setStatus(UserStatus.ACTIVE);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateByAdmin(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        userMapper.updateForAdmin(user, userUpdateRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateSelf (Long userId, UserSelfUpdateRequest userSelfUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        userMapper.updateForUser(user, userSelfUpdateRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        UserRole role = null;
        try {
            role = UserRole.valueOf(keyword.toUpperCase());
        } catch (Exception ignored) {}
        return userRepository.findByEmailContainingIgnoreCaseOrRole(keyword, role, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Để tương thích với UserDetailsService, nhưng thực chất username là email
        return loadUserByEmail(username);
    }
}
