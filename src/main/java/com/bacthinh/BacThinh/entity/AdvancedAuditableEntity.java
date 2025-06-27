package com.bacthinh.BacThinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;

@MappedSuperclass
@Getter
@Setter
public abstract class AdvancedAuditableEntity extends BaseAuditableEntity {

    @Column(name = "created_ip", length = 45)
    private String createdIp;

    @Column(name = "updated_ip", length = 45)
    private String updatedIp;

    @Column(name = "created_user_agent", length = 500)
    private String createdUserAgent;

    @Column(name = "updated_user_agent", length = 500)
    private String updatedUserAgent;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @PrePersist
    protected void onCreate() {
        this.timezone = ZoneId.systemDefault().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        // Additional logic can be added here
    }
}

