package ru.practicum.mainservice.event.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureEventDateValidator.class)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface  FutureEventDate {
    String message() default "Event date must be in future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
