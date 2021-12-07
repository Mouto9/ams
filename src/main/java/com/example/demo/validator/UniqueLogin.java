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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueLoginValidator.class)
public @interface UniqueLogin {
    /**
     * @return エラーメッセージ。
     */
    String message() default "この職員IDは既に登録されています";
    /**
     * @return 初期値。
     */
    Class<?>[] groups() default{};
    /**
     * @return 初期値。
     */
    Class<? extends Payload>[] payload() default{};
    /**
     * @return 被バリデーションフィールドリスト。
     */
    String[] fields();

	  /**
	 * リスト。
	 */
	public @interface List {
		  /**
		 * @return 値。
		 */
		UniqueLogin[] value();
	  }
}
