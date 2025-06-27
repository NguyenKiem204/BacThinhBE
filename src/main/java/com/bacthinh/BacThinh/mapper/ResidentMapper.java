package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.ResidentCreationRequest;
import com.bacthinh.BacThinh.dto.request.ResidentUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ResidentResponse;
import com.bacthinh.BacThinh.entity.Resident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResidentMapper {
    
    @Mapping(target = "familyId", source = "family.id")
    @Mapping(target = "familyCode", source = "family.familyCode")
    ResidentResponse toResponse(Resident resident);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "sacraments", ignore = true)
    @Mapping(target = "eventRegistrations", ignore = true)
    @Mapping(target = "groupMemberships", ignore = true)
    Resident toEntity(ResidentCreationRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "sacraments", ignore = true)
    @Mapping(target = "eventRegistrations", ignore = true)
    @Mapping(target = "groupMemberships", ignore = true)
    void updateEntity(@MappingTarget Resident resident, ResidentUpdateRequest request);
} 