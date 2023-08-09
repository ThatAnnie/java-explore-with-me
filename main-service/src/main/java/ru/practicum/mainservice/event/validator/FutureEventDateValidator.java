package ru.practicum.mainservice.event.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureEventDateValidator implements ConstraintValidator<FutureEventDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext cxt) {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}
