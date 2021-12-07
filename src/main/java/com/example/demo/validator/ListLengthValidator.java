package com.example.demo.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 勤怠情報登録時のバリデータ。
 */
public class ListLengthValidator implements ConstraintValidator<ListLength, List<Long>> {
	
    /**
     * バリデーション。
     */
    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
    	if(value.size() == 0) {//チェックボックスがからかどうか
    		return false;
    	}
    	return true;
    }
}
