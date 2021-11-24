package com.example.demo.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ListLengthValidator implements ConstraintValidator<ListLength, List> {
	
    @Override
    public boolean isValid(List value, ConstraintValidatorContext context) {
    	value.stream().forEach(System.out::println);
    	if(value.size() == 0) {//チェックボックスがからかどうか
    		System.out.println("d");
    		return false;
    	}
    	return true;
    }
}
