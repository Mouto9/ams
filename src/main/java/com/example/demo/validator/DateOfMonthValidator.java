package com.example.demo.validator;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class DateOfMonthValidator implements ConstraintValidator<DateOfMonth, Object> {

	// バリデーション対象の変数の値を入れる
	  private String[] fields;
	  // アノテーションクラス（下記2）で設定しているエラーメッセージが入る
	  private String message;

	  /**
	   * 初期化処理 
	   * ()内のクラスは下記2のアノテーションクラス
	   */
	  public void initialize(DateOfMonth annotation) {
	    this.fields = annotation.fields();
	    this.message = annotation.message();
	   }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {//ここからstartとendのいいわるいの判別を行う
    	BeanWrapper beanWrapper = new BeanWrapperImpl(value);
    	Date s = (Date)beanWrapper.getPropertyValue(fields[0]);
    	Date e = (Date)beanWrapper.getPropertyValue(fields[1]);
			
		Calendar c = Calendar.getInstance();
		c.setTime(s);
		int s2 = c.get(Calendar.MONTH);
		int s1 = c.get(Calendar.DAY_OF_MONTH);
		int sy = c.get(Calendar.YEAR);
		c.setTime(e);
		int e2 = c.get(Calendar.MONTH);
		int e1 = c.get(Calendar.DAY_OF_MONTH);
		int ey = c.get(Calendar.YEAR);
		
		if(!(sy >= 2000 && sy <= 3000) || !(ey >= 2000 && ey <= 3000))  {
			context.disableDefaultConstraintViolation();
		    context.buildConstraintViolationWithTemplate("想定以上又は以下の西暦が入力されています").addPropertyNode(fields[0])
		        .addConstraintViolation();
		    context.buildConstraintViolationWithTemplate("想定以上又は以下の西暦が入力されています").addPropertyNode(fields[1])
	        .addConstraintViolation();
			
			return false;
		}
		
		if(e2 != s2) {
			context.disableDefaultConstraintViolation();
		    context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[0])
		        .addConstraintViolation();
		    context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[1])
	        .addConstraintViolation();
			return false;
		}else if(e1 < s1){
			context.disableDefaultConstraintViolation();
		    context.buildConstraintViolationWithTemplate("開始日と終了日が逆転しています").addPropertyNode(fields[0])
		        .addConstraintViolation();
		    context.buildConstraintViolationWithTemplate("開始日と終了日が逆転しています").addPropertyNode(fields[1])
	        .addConstraintViolation();
			
			return false;
		}else {
			return true;
		}

		
    }
}
