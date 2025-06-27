package com.bacthinh.BacThinh.config.audit;

import com.bacthinh.BacThinh.entity.BaseAuditableEntity;

public class AuditContext {
    private static final ThreadLocal<BaseAuditableEntity> OLD_ENTITY = new ThreadLocal<>();

    public static void setOldEntity(BaseAuditableEntity entity) {
        OLD_ENTITY.set(entity);
    }

    public static BaseAuditableEntity getAndClearOldEntity() {
        BaseAuditableEntity entity = OLD_ENTITY.get();
        OLD_ENTITY.remove();
        return entity;
    }
}
