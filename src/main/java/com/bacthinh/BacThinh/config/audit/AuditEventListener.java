package com.bacthinh.BacThinh.config.audit;

import com.bacthinh.BacThinh.entity.AuditAction;
import com.bacthinh.BacThinh.entity.AuditEvent;
import com.bacthinh.BacThinh.entity.BaseAuditableEntity;
import com.bacthinh.BacThinh.service.AuditEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class AuditEventListener {

    @Autowired
    @Lazy
    private AuditEventService auditEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @PrePersist
    public void prePersist(BaseAuditableEntity entity) {
        log.info("=== PrePersist triggered for entity: {} === ", entity.getClass().getSimpleName());
        createAuditEvent(entity, AuditAction.CREATE, null, entity);
    }

    @PreUpdate
    public void preUpdate(BaseAuditableEntity newEntity) {
        log.info("=== PreUpdate triggered for entity: {} === ", newEntity.getClass().getSimpleName());
        BaseAuditableEntity oldEntity = AuditContext.getAndClearOldEntity();
        if (oldEntity != null) {
            createAuditEvent(newEntity, AuditAction.UPDATE, oldEntity, newEntity);
        } else {
            log.warn("Old entity not found in AuditContext for update of {}. Audit event might be incomplete.", newEntity.getClass().getSimpleName());
            createAuditEvent(newEntity, AuditAction.UPDATE, null, newEntity);
        }
    }

    @PreRemove
    public void preRemove(BaseAuditableEntity entity) {
        log.info("=== PreRemove triggered for entity: {} === ", entity.getClass().getSimpleName());
        createAuditEvent(entity, AuditAction.DELETE, entity, null);
    }

    private void createAuditEvent(BaseAuditableEntity entity, AuditAction action,
                                  BaseAuditableEntity oldEntity, BaseAuditableEntity newEntity) {
        try {
            String entityId = getEntityId(entity);
            String entityType = entity.getClass().getSimpleName();

            AuditEvent auditEvent = AuditEvent.builder()
                    .entityId(entityId)
                    .entityType(entityType)
                    .action(action)
                    .timestamp(LocalDateTime.now())
                    .sessionId(getSessionId())
                    .requestId(getRequestId())
                    .clientIp(getClientIp())
                    .isDeleted(false)
                    .build();

            if (oldEntity != null) {
                auditEvent.setOldValues(serializeAuditedEntityData(oldEntity));
            }

            if (newEntity != null) {
                auditEvent.setNewValues(serializeAuditedEntityData(newEntity));
            }

            if (action == AuditAction.UPDATE) {
                auditEvent.setChangedFields(getChangedFields(oldEntity, newEntity));
            }

            auditEventService.saveAuditEvent(auditEvent);

        } catch (Exception e) {
            log.error("Error creating audit event for entity: {}", entity.getClass().getSimpleName(), e);
        }
    }

    private String getEntityId(BaseAuditableEntity entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(entity);
            return idValue != null ? idValue.toString() : "unknown";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Could not get ID for entity: {}", entity.getClass().getSimpleName(), e);
            return "unknown";
        }
    }

    private String serializeAuditedEntityData(BaseAuditableEntity entity) {
        try {
            Map<String, Object> basicFields = new HashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (!field.isSynthetic() && !field.getName().equals("serialVersionUID")) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value == null || !(value instanceof java.util.Collection || value instanceof java.util.Map ||
                            field.isAnnotationPresent(OneToOne.class) ||
                            field.isAnnotationPresent(OneToMany.class) ||
                            field.isAnnotationPresent(ManyToOne.class) ||
                            field.isAnnotationPresent(ManyToMany.class))) {
                        basicFields.put(field.getName(), value);
                    }
                }
            }
            return objectMapper.writeValueAsString(basicFields);
        } catch (Exception e) {
            log.warn("Could not serialize basic entity data for {}: {}", entity.getClass().getSimpleName(), e.getMessage());
            return "{}";
        }
    }

    private String getChangedFields(BaseAuditableEntity oldEntity, BaseAuditableEntity newEntity) {
        if (oldEntity == null || newEntity == null) {
            return "{}";
        }
        Map<String, Object> changes = new HashMap<>();
        try {
            Class<?> entityClass = oldEntity.getClass();
            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isSynthetic() && !field.getName().equals("serialVersionUID")) {
                    field.setAccessible(true);
                    Object oldValue = field.get(oldEntity);
                    Object newValue = field.get(newEntity);

                    if (oldValue == null || !(oldValue instanceof java.util.Collection || oldValue instanceof java.util.Map ||
                            field.isAnnotationPresent(OneToOne.class) ||
                            field.isAnnotationPresent(OneToMany.class) ||
                            field.isAnnotationPresent(ManyToOne.class) ||
                            field.isAnnotationPresent(ManyToMany.class))) {
                        if (!java.util.Objects.equals(oldValue, newValue)) {
                            changes.put(field.getName(), Map.of("old", oldValue, "new", newValue));
                        }
                    }
                }
            }
            return objectMapper.writeValueAsString(changes);
        } catch (Exception e) {
            log.warn("Could not get changed fields for {}: {}", oldEntity.getClass().getSimpleName(), e.getMessage());
            return "{}";
        }
    }

    private String getSessionId() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            return request.getSession().getId();
        } catch (IllegalStateException e) {
            return "no_http_session";
        } catch (Exception e) {
            log.warn("Error getting session ID: {}", e.getMessage());
            return "unknown_session";
        }
    }

    private String getRequestId() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            String requestId = request.getHeader("X-Request-ID");
            return requestId != null ? requestId : UUID.randomUUID().toString();
        } catch (IllegalStateException e) {
            return "no_http_request_" + UUID.randomUUID().toString();
        } catch (Exception e) {
            log.warn("Error getting request ID: {}", e.getMessage());
            return UUID.randomUUID().toString();
        }
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            if (attr == null) {
                return "no_request_context";
            }
            HttpServletRequest request = attr.getRequest();

            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            if (ipAddress != null && ipAddress.contains(",")) {
                ipAddress = ipAddress.split(",")[0].trim();
            }
            return ipAddress != null ? ipAddress : "unknown_ip";
        } catch (Exception e) {
            log.warn("Error getting client IP: {}", e.getMessage());
            return "error_getting_ip";
        }
    }
}