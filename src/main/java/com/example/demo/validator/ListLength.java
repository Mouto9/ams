package com.example.demo.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * バリデータ。
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListLengthValidator.class)
public @interface ListLength {
    /**
     * @return エラーメッセージ。
     */
    String message() default "チェックされていません";
    /**
     * @return 初期値。
     */
    Class<?>[] groups() default{};
    /**
     * @return 初期値。
     */
    Class<? extends Payload>[] payload() default{};
}
