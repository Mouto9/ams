package com.example.demo.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.validator.Attend;

import lombok.Getter;
import lombok.Setter;

/**
 * バッファ。
 */
@Getter
@Setter
@Attend(fields= {"attend","user"})
public class CalendarBuf {
	private String title;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date start;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date end;
	
	private UserInfo user;

	private String color;
	private long id;
	
	private String attend;
	
	public int hashCode(){
		int result = 37;
		result = result * 31 + start.hashCode();
        result = result * 31 + title.hashCode();
        return result;
	}
	public boolean equals(Object obj){
        CalendarBuf tmp = (CalendarBuf)obj;
        if (obj == this)
            return true;
        if (obj == null) 
        	return false;
        if (!(obj instanceof CalendarBuf))
            return false;
        if(tmp.getStart().equals(this.getStart()) && tmp.getTitle().equals(this.getTitle())){
                return true;
        }else{
                return false;
       }
}
}
