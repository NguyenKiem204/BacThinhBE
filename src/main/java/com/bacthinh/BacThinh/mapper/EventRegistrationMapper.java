package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.EventRegistrationRequest;
import com.bacthinh.BacThinh.dto.response.EventRegistrationResponse;
import com.bacthinh.BacThinh.entity.EventRegistration;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventRegistrationMapper {
    EventRegistrationResponse toResponse(EventRegistration eventRegistration);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventRegistrationFromDto(EventRegistrationRequest request, @MappingTarget EventRegistration eventRegistration);
} 