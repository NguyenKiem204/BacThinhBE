package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.EventRegistrationRequest;
import com.bacthinh.BacThinh.dto.response.EventRegistrationResponse;
import com.bacthinh.BacThinh.entity.Event;
import com.bacthinh.BacThinh.entity.EventRegistration;
import com.bacthinh.BacThinh.entity.Resident;
import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.mapper.EventRegistrationMapper;
import com.bacthinh.BacThinh.repository.EventRegistrationRepository;
import com.bacthinh.BacThinh.repository.EventRepository;
import com.bacthinh.BacThinh.repository.ResidentRepository;
import com.bacthinh.BacThinh.repository.UserRepository;
import com.bacthinh.BacThinh.service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final EventRegistrationMapper eventRegistrationMapper;

    @Override
    @Transactional
    public EventRegistrationResponse createRegistration(EventRegistrationRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new NotFoundException("Event", request.getEventId()));

        Resident resident = residentRepository.findById(request.getResidentId())
                .orElseThrow(() -> new NotFoundException("Resident", request.getResidentId()));

        User registeredByUser = null;
        if (request.getRegisteredByUserId() != null) {
            registeredByUser = userRepository.findById(request.getRegisteredByUserId())
                    .orElseThrow(() -> new NotFoundException("User", request.getRegisteredByUserId()));
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setResident(resident);
        registration.setRegisteredByUser(registeredByUser);
        registration.setNotes(request.getNotes());

        registration = eventRegistrationRepository.save(registration);
        return eventRegistrationMapper.toResponse(registration);
    }

    @Override
    @Transactional
    public EventRegistrationResponse updateRegistration(Long id, EventRegistrationRequest request) {
        EventRegistration registration = eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("EventRegistration", id));

        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new NotFoundException("Event", request.getEventId()));
            registration.setEvent(event);
        }

        if (request.getResidentId() != null) {
            Resident resident = residentRepository.findById(request.getResidentId())
                    .orElseThrow(() -> new NotFoundException("Resident", request.getResidentId()));
            registration.setResident(resident);
        }

        if (request.getRegisteredByUserId() != null) {
            User registeredByUser = userRepository.findById(request.getRegisteredByUserId())
                    .orElseThrow(() -> new NotFoundException("User", request.getRegisteredByUserId()));
            registration.setRegisteredByUser(registeredByUser);
        }

        if (request.getNotes() != null) {
            registration.setNotes(request.getNotes());
        }

        registration = eventRegistrationRepository.save(registration);
        return eventRegistrationMapper.toResponse(registration);
    }

    @Override
    @Transactional
    public void deleteRegistration(Long id) {
        if (!eventRegistrationRepository.existsById(id)) {
            throw new NotFoundException("EventRegistration", id);
        }
        eventRegistrationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EventRegistrationResponse getRegistration(Long id) {
        EventRegistration registration = eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("EventRegistration", id));
        return eventRegistrationMapper.toResponse(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventRegistrationResponse> getAllRegistrations(Pageable pageable) {
        return eventRegistrationRepository.findAll(pageable)
                .map(eventRegistrationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventRegistrationResponse> searchRegistrations(Long eventId, Long residentId, Long registeredByUserId, Pageable pageable) {
        Page<EventRegistration> page = eventRegistrationRepository.searchRegistrations(eventId, residentId, registeredByUserId, pageable);
        return page.map(eventRegistrationMapper::toResponse);
    }
} 