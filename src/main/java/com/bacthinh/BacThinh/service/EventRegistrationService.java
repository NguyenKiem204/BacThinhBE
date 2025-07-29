package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.EventRegistrationRequest;
import com.bacthinh.BacThinh.dto.response.EventRegistrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRegistrationService {
    EventRegistrationResponse createRegistration(EventRegistrationRequest request);
    EventRegistrationResponse updateRegistration(Long id, EventRegistrationRequest request);
    void deleteRegistration(Long id);
    EventRegistrationResponse getRegistration(Long id);
    Page<EventRegistrationResponse> getAllRegistrations(Pageable pageable);
    Page<EventRegistrationResponse> searchRegistrations(Long eventId, Long residentId, Long registeredByUserId, Pageable pageable);
} 