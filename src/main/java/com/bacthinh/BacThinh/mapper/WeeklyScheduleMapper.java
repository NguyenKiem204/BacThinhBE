package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.CreateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.UpdateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.response.PagedWeeklyScheduleResponse;
import com.bacthinh.BacThinh.dto.response.WeeklyScheduleResponse;
import com.bacthinh.BacThinh.entity.WeeklySchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

@Mapper(componentModel = "spring", uses = {DailyScheduleMapper.class})
public interface WeeklyScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "weekNumber", expression = "java(getWeekNumber(request.getWeekStartDate()))")
    @Mapping(target = "year", expression = "java(getYear(request.getWeekStartDate()))")
    @Mapping(target = "status", constant = "DRAFT")
    WeeklySchedule toEntity(CreateWeeklyScheduleRequest request);

    WeeklyScheduleResponse toResponse(WeeklySchedule entity);

    List<WeeklyScheduleResponse> toResponseList(List<WeeklySchedule> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "weekStartDate", ignore = true)
    @Mapping(target = "weekEndDate", ignore = true)
    @Mapping(target = "weekNumber", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget WeeklySchedule entity, UpdateWeeklyScheduleRequest request);

    // Helper methods
    default Integer getWeekNumber(LocalDate date) {
        if (date == null) return null;
        return date.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfYear());
    }

    default Integer getYear(LocalDate date) {
        if (date == null) return null;
        return date.getYear();
    }

    default PagedWeeklyScheduleResponse toPagedResponse(Page<WeeklySchedule> page) {
        return PagedWeeklyScheduleResponse.builder()
                .content(toResponseList(page.getContent())) // <-- convert entity list to DTO list
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(!page.isLast())
                .hasPrevious(!page.isFirst())
                .build();
    }
}
