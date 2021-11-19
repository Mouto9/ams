package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarBuf {
	private String title;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date start;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date end;
	
	private List<UserInfo> userList;
	private List<Long> userInfoIdList = new ArrayList<>();
	private int number;
	private String color;
	
	public int hashCode(){
		int result = 37;
		result = result * 31 + start.hashCode();
        result = result * 31 + number;
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
        if(tmp.getStart().equals(this.getStart()) && tmp.getTitle().equals(this.getTitle()) && tmp .getNumber() == this.getNumber()){
                return true;
        }else{
                return false;
       }
}
}
