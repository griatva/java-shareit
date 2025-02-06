package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.enums.Status;

public class StatusValidator implements ConstraintValidator<ValidStatus, Status> {

    @Override
    public boolean isValid(Status value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            Status.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
