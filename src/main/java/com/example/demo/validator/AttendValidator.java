package com.example.demo.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ScheduleRepository;

/**
 * 出勤情報登録時のバリデータ。
 */
public class AttendValidator implements ConstraintValidator<Attend, Object> {
	private final AttendanceRepository attendance;
	private final ScheduleRepository schedule;
	/**
	 * コンストラクタ。
	 */
	public AttendValidator() {
		this.attendance = null;
		this.schedule = null;
	}
    /**
     * @param attendance 出勤情報。
     * @param schedule 勤怠情報。
     */
    @Autowired
    public AttendValidator(AttendanceRepository attendance,ScheduleRepository schedule) {
        this.attendance = attendance;
        this.schedule = schedule;
    }
	
	// バリデーション対象の変数の値を入れる
	  private String[] fields;
	  // アノテーションクラス（下記2）で設定しているエラーメッセージが入る
	  private String message;
	  /**
	   * 初期化処理 
	   * ()内のクラスは下記2のアノテーションクラス
	   */
	  /**
	 * 初期。
	 */
	public void initialize(Attend annotation) {
	    this.fields = annotation.fields();
	    this.message = annotation.message();
	   }
    /**
     * バリデーション。
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
    	BeanWrapper beanWrapper = new BeanWrapperImpl(value);
    	String d = beanWrapper.getPropertyValue(fields[0]).toString();//attend/日付
    	UserInfo user = (UserInfo)beanWrapper.getPropertyValue(fields[1]);//user
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date s = sdf.parse(d + " 00:00");//始め
			Date e = sdf.parse(d + " 23:59");//終わり
			if(attendance == null || schedule == null){
				context.disableDefaultConstraintViolation();
			    context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[0])
		        .addConstraintViolation();
				return false;
			}else if(attendance.existsByAttendanceTimeBetweenAndUserInfo(s,e,user)){
				context.disableDefaultConstraintViolation();
			    context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[0])
		        .addConstraintViolation();
				return false;
			}else if(schedule.findByScheduleDateAndUserInfo(s,user) == null 
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("土曜")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("日曜")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("休日")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("特休")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("夏休")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("出張")
					|| schedule.findByScheduleDateAndUserInfo(s,user).getScheduleName().equals("年休")) {
				context.disableDefaultConstraintViolation();
			    context.buildConstraintViolationWithTemplate("出勤や時休等の予定がありません").addPropertyNode(fields[0])
		        .addConstraintViolation();
			    return false;
			}else {
				return true;
			}
		} catch (ParseException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			return false;
		}//登録したい日時
    	
    	

    }
}
