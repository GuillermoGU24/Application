package co.com.crediya.api.util;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import reactor.core.publisher.Mono;

import java.util.Set;

public class ValidationUtil {

    public static <T> Mono<T> validate(T object, Validator validator) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(object);
    }
}
