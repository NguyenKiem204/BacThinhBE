package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.CreateMassRequest;
import com.bacthinh.BacThinh.dto.request.UpdateMassRequest;
import com.bacthinh.BacThinh.dto.response.MassResponse;
import com.bacthinh.BacThinh.entity.Mass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MassMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dailySchedule", ignore = true)
    Mass toEntity(CreateMassRequest request);

    MassResponse toResponse(Mass entity);

    List<MassResponse> toResponseList(List<Mass> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dailySchedule", ignore = true)
    void updateEntity(@MappingTarget Mass entity, UpdateMassRequest request);
}
