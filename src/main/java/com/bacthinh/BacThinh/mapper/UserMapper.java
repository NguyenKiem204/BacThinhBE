package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.UserCreationRequest;
import com.bacthinh.BacThinh.dto.request.UserSelfUpdateRequest;
import com.bacthinh.BacThinh.dto.request.UserUpdateRequest;
import com.bacthinh.BacThinh.dto.response.UserResponse;
import com.bacthinh.BacThinh.entity.Resident;
import com.bacthinh.BacThinh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "resident", source = "residentId", qualifiedByName = "mapResidentId")
    void updateForUser(@MappingTarget User user, UserSelfUpdateRequest request);

    @Mapping(target = "resident", source = "residentId", qualifiedByName = "mapResidentId")
    void updateForAdmin(@MappingTarget User user, UserUpdateRequest request);

    @Named("mapResidentId")
    default Resident mapResidentId(Long id) {
        if (id == null) return null;
        Resident r = new Resident();
        r.setId(id);
        return r;
    }
}