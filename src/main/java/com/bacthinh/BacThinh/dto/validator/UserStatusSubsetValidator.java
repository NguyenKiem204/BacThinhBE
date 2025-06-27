package com.bacthinh.BacThinh.dto.validator;

import com.bacthinh.BacThinh.entity.UserStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class UserStatusSubsetValidator implements ConstraintValidator<UserStatusSubset, UserStatus> {
    private UserStatus[] userStatuses;

    @Override
    public void initialize(UserStatusSubset constraint) {
        this.userStatuses = constraint.anyOf();
    }

    @Override
    public boolean isValid(UserStatus value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(userStatuses).contains(value);
    }
}
