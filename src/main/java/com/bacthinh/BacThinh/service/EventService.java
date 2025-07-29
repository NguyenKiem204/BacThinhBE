package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.EventRequest;
import com.bacthinh.BacThinh.dto.response.EventDetailResponse;
import com.bacthinh.BacThinh.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventResponse updateEvent(Long id, EventRequest request);
    void deleteEvent(Long id);
    EventResponse getEvent(Long id);
    Page<EventResponse> getAllEvents(Pageable pageable);
    Page<EventResponse> searchEvents(String keyword, LocalDateTime startTimeFrom, LocalDateTime startTimeTo, Pageable pageable);
    EventDetailResponse getEventDetail(Long eventId);
} 