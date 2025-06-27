package com.bacthinh.BacThinh.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotFoundException extends RuntimeException {
    private final String resourceName;
    private final Object resourceId;

    public NotFoundException(String resourceName, Object resourceId) {
        super(resourceName + " with id " + resourceId + " not found");
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
