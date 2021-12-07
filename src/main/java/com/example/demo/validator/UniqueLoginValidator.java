package com.example.demo.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repository.UserInfoRepository;

/**
 * 職員情報管理時のバリデータ。
 */
public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, Object> {
	
	// バリデーション対象の変数の値を入れる
	  private String[] fields;
	  // アノテーションクラス（下記2）で設定しているエラーメッセージが入る
	  private String message;

    private final UserInfoRepository userRepository;
	  public void initialize(UniqueLogin annotation) {
		    this.fields = annotation.fields();
		    this.message = annotation.message();
		   }
	  
    /**
     * 初期化。
     */
    public UniqueLoginValidator() {
        this.userRepository = null;
    }

    /**
     * @param userRepository 職員情報リポジトリ。
     */
    @Autowired
    public UniqueLoginValidator(UserInfoRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * バリデーション。
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
    	BeanWrapper beanWrapper = new BeanWrapperImpl(value);
    	String userId = beanWrapper.getPropertyValue(fields[1]).toString();
    	if(userRepository == null) {
    		return true;
    	}
    	if(Objects.isNull(beanWrapper.getPropertyValue(fields[0]))) {//テーブルidがあるかないかの確認
    		if(userRepository.findByUsername(userId) != null) {//テーブルidがなくて入力した新規職員IDがあったら
    			context.disableDefaultConstraintViolation();
    			context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[1])
    				.addConstraintViolation();
    			return false;
    		}
    		return true;
    	}else {
    		long id = (long)beanWrapper.getPropertyValue(fields[0]);
    		if(userRepository.findByUsername(userId).getId() != id) {
    			context.disableDefaultConstraintViolation();
    			context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[1])
    				.addConstraintViolation();
    			return false;
    		}
    		return true;
    	}
    }
}
