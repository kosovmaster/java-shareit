package ru.practicum.shareit.booking.validator.create;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.validator.BookingDtoCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Slf4j
public class ValidBookingCreateImpl implements ConstraintValidator<ValidBookingCreate, BookingDtoCreate> {
    private final LocalDateTime localDateTime = LocalDateTime.now();

    @Override
    public void initialize(ValidBookingCreate constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoCreate booking, ConstraintValidatorContext constraintValidatorContext) {
        if (booking.getItemId() < 1) {
            log.warn("Item id is less than 1 and equal={}", booking.getItemId());
            return false;
        } else if (booking.getStart() == null || booking.getEnd() == null
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(localDateTime)
                || booking.getStart().isEqual(booking.getEnd())
                || booking.getEnd().isBefore(localDateTime)) {
            log.warn("The start time={} of the booking is after the end time={}, or it's before the current time={}",
                    booking.getStart(), booking.getEnd(), localDateTime);
            return false;
        }
        return true;
    }
}
