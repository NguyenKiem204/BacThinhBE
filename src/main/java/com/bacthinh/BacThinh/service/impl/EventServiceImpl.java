package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.EventRequest;
import com.bacthinh.BacThinh.dto.response.EventDetailResponse;
import com.bacthinh.BacThinh.dto.response.EventRegistrationResponse;
import com.bacthinh.BacThinh.dto.response.EventResponse;
import com.bacthinh.BacThinh.entity.Event;
import com.bacthinh.BacThinh.entity.EventRegistration;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.mapper.EventMapper;
import com.bacthinh.BacThinh.repository.EventRepository;
import com.bacthinh.BacThinh.repository.EventRegistrationRepository;
import com.bacthinh.BacThinh.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = eventMapper.toEvent(request);
        event = eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
        eventMapper.updateEventFromDto(request, event);
        event = eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> searchEvents(String keyword, LocalDateTime startTimeFrom, LocalDateTime startTimeTo, Pageable pageable) {
        Page<Event> page = eventRepository.searchEvents(keyword, startTimeFrom, startTimeTo, pageable);
        return page.map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event", eventId));

        // Lấy số lượng đăng ký
        Long totalRegistrations = eventRegistrationRepository.countByEventId(eventId);

        // Lấy danh sách đăng ký
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);

        // Map sang response
        EventResponse eventResponse = eventMapper.toResponse(event);
        
        return EventDetailResponse.builder()
                .id(eventResponse.getId())
                .name(eventResponse.getName())
                .description(eventResponse.getDescription())
                .startTime(eventResponse.getStartTime())
                .endTime(eventResponse.getEndTime())
                .location(eventResponse.getLocation())
                .totalRegistrations(totalRegistrations)
                .registrations(registrations.stream()
                        .map(this::mapToRegistrationResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private EventRegistrationResponse mapToRegistrationResponse(EventRegistration registration) {
        return EventRegistrationResponse.builder()
                .id(registration.getId())
                .eventId(registration.getEvent().getId())
                .eventName(registration.getEvent().getName())
                .residentId(registration.getResident().getId())
                .residentName(registration.getResident().getName())
                .registeredByUserId(registration.getRegisteredByUser() != null ? registration.getRegisteredByUser().getId() : null)
                .registeredByUserName(registration.getRegisteredByUser() != null ? registration.getRegisteredByUser().getName() : null)
                .status(registration.getStatus())
                .notes(registration.getNotes())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }
} 