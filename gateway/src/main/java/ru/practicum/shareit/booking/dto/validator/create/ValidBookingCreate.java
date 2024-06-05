package ru.practicum.shareit.booking.dto.validator.create;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBookingCreateImpl.class)
public @interface ValidBookingCreate {
    String message() default "The start date and time must not be later than the end date and time. " +
            "The start date and time and the end date and time must not be in the past. " +
            "The start date and time and the end date and time must not be equal";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "";
}
