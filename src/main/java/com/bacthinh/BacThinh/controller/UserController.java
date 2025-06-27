package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.UserCreationRequest;
import com.bacthinh.BacThinh.dto.request.UserSelfUpdateRequest;
import com.bacthinh.BacThinh.dto.request.UserUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import com.bacthinh.BacThinh.dto.response.UserResponse;
import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.entity.UserStatus;
import com.bacthinh.BacThinh.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller", description = "Manage users: create, update, search, delete users")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new user", description = "ADMIN can create a new user.")
    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest UserRequest) {
        UserResponse userResponse = userService.createUser(UserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userResponse, "User create successfully!"));
    }

    @Operation(summary = "Admin updates user", description = "ADMIN updates any user's information.")
    @PutMapping("/admin-update/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        UserResponse userResponse = userService.updateByAdmin(id, userUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userResponse, "User update successfully!"));
    }

    @Operation(summary = "User self-update", description = "User updates their own information.")
    @PutMapping("/self-update/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateSelf(@PathVariable("id") Long id, @RequestBody @Valid UserSelfUpdateRequest userSelfUpdateRequest) {
        UserResponse userResponse = userService.updateSelf(id, userSelfUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User self-update successfully!"));
    }

    @Operation(summary = "Get user by ID", description = "ADMIN gets user details by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User retrieved successfully!"));
    }

    @Operation(summary = "Get all users", description = "ADMIN gets all users with pagination and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully!"));
    }

    @Operation(summary = "Search users", description = "ADMIN searches users by keyword (email or role).")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Search results retrieved successfully!"));
    }

    @Operation(summary = "Delete user", description = "ADMIN deletes a user by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully!", "User deleted successfully!"));
    }
}
