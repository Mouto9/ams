package com.example.demo.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.UserInfoRepository;

public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {

    private final UserInfoRepository userRepository;

    public UniqueLoginValidator() {
        this.userRepository = null;
    }

    @Autowired
    public UniqueLoginValidator(UserInfoRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	if(userRepository == null || userRepository.findByUsername(value) == null) {
    		return true;
    	}else {
    		return false;
    	}
    }
}
