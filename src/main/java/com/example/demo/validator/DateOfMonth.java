package com.example.demo.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * バリデータ。
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOfMonthValidator.class)
public @interface DateOfMonth {
    /**
     * @return エラーメッセージ。
     */
    String message() default "開始日と終了日の月が異なっています";
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
	    DateOfMonth[] value();
	  }

}
