package com.example.demo.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.AttendanceRepository;

public class AttendValidator implements ConstraintValidator<Attend, String> {

    private final AttendanceRepository attend;

    public AttendValidator() {
        this.attend = null;
    }

    @Autowired
    public AttendValidator(AttendanceRepository attend) {
        this.attend = attend;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	if(!(attend == null)) {
    		return true;
    	}else {
    		return false;
    	}
    }
}
