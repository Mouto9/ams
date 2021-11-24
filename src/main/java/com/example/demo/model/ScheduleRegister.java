package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.validator.DateOfMonth;
import com.example.demo.validator.ListLength;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@DateOfMonth(fields= {"start","end"})
public class ScheduleRegister {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date start;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date end;
	private List<UserInfo> userList;
	@ListLength
	private List<Long> userInfoIdList = new ArrayList<>();
}