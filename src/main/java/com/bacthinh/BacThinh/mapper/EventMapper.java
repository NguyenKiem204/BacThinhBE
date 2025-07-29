package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.EventRequest;
import com.bacthinh.BacThinh.dto.response.EventResponse;
import com.bacthinh.BacThinh.entity.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventRequest request);
    EventResponse toResponse(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromDto(EventRequest request, @MappingTarget Event event);
} 