package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.CreateDailyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.UpdateDailyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.UpdateMassRequest;
import com.bacthinh.BacThinh.dto.response.DailyScheduleResponse;
import com.bacthinh.BacThinh.entity.DailySchedule;
import com.bacthinh.BacThinh.entity.Mass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {MassMapper.class})
public interface DailyScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "weeklySchedule", ignore = true)
    DailySchedule toEntity(CreateDailyScheduleRequest request);

    DailyScheduleResponse toResponse(DailySchedule entity);

    List<DailyScheduleResponse> toResponseList(List<DailySchedule> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "weeklySchedule", ignore = true)
    void updateEntity(@MappingTarget DailySchedule entity, UpdateDailyScheduleRequest request);

    default void mergeMasses(@MappingTarget DailySchedule entity, List<UpdateMassRequest> massRequests) {
        if (massRequests == null) return;

        Map<Long, Mass> existingMasses = entity.getMasses().stream()
                .filter(mass -> mass.getId() != null)
                .collect(Collectors.toMap(Mass::getId, Function.identity()));

        entity.getMasses().clear();

        for (UpdateMassRequest request : massRequests) {
            if (request.getId() != null && existingMasses.containsKey(request.getId())) {
                Mass existingMass = existingMasses.get(request.getId());
                updateMassFromRequest(existingMass, request);
                entity.getMasses().add(existingMass);
            } else {
                Mass newMass = createMassFromRequest(request);
                newMass.setDailySchedule(entity);
                entity.getMasses().add(newMass);
            }
        }
    }

    default Mass createMassFromRequest(UpdateMassRequest request) {
        return Mass.builder()
                .time(request.getTime())
                .type(request.getType())
                .celebrant(request.getCelebrant())
                .note(request.getNote())
                .specialName(request.getSpecialName())
                .isSolemn(request.getIsSolemn())
                .build();
    }

    default void updateMassFromRequest(Mass mass, UpdateMassRequest request) {
        mass.setTime(request.getTime());
        mass.setType(request.getType());
        mass.setCelebrant(request.getCelebrant());
        mass.setNote(request.getNote());
        mass.setSpecialName(request.getSpecialName());
        mass.setIsSolemn(request.getIsSolemn());
    }
}
