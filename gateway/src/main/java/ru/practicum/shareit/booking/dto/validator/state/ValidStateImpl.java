package ru.practicum.shareit.booking.dto.validator.state;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class ValidStateImpl implements ConstraintValidator<ValidState, String> {
    @Override
    public void initialize(ValidState constraintAnnotation) {
    }

    @Override
    public boolean isValid(String state, ConstraintValidatorContext constraintValidatorContext) {
        if (state == null) {
            return true;
        }

        boolean isState = state.equals("CURRENT") || state.equals("PAST")
                || state.equals("FUTURE") || state.equals("WAITING")
                || state.equals("REJECTED") || state.equals("ALL");

        if (!isState) {
            log.warn("State booking={} not exists", state);
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Unknown state: " + state)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
