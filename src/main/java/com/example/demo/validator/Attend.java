package com.example.demo.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AttendValidator.class)
public @interface Attend {
    String message() default "様式に合っていません";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
