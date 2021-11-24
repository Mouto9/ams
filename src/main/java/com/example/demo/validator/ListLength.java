package com.example.demo.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListLengthValidator.class)
public @interface ListLength {
    String message() default "チェックされていません";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}
