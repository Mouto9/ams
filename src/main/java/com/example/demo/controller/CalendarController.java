package com.example.demo.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Attendance;
import com.example.demo.model.CalendarBuf;
import com.example.demo.model.Schedule;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
public class CalendarController {
	private final ScheduleRepository scheduleRepository;
	private final AttendanceRepository attendedRepository;
	private final UserInfoRepository userRepository;
    @GetMapping("/myScheduleURL")//mypage用
    public String getEvents(Authentication loginuser) {
        String jsonMsg = null;
        try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            DateFormat attendedFormat = new SimpleDateFormat("HH:mm");
            
            //長いからlistに格納
            List<Schedule> list = new ArrayList<>();
            list = scheduleRepository.findByUserInfo(userRepository.findByUsername(loginuser.getName()));
            
            List<CalendarBuf> schedules = new ArrayList<>();
            for(int i  = 0 ; i < list.size(); i ++) {//勤怠予定をjasonに記述する
            	CalendarBuf schedule = new CalendarBuf();
            	schedule.setTitle(list.get(i).getScheduleName());
            	schedule.setStart(list.get(i).getScheduleDate());
            	if(list.get(i).getScheduleName().equals("休暇") || list.get(i).getScheduleName().equals("年休")) {
            		schedule.setColor("red");
            	}
            	schedules.add(schedule);
            }
            List<Attendance> attendedList = new ArrayList<>();
            attendedList = attendedRepository.findByUserInfo(userRepository.findByUsername(loginuser.getName()));
            for(int i = 0; i < attendedList.size(); i ++) {//出勤時刻をjasonに記述する
            	CalendarBuf schedule = new CalendarBuf();
            	schedule.setTitle(attendedFormat.format(attendedList.get(i).getAttendanceTime()));
            	schedule.setStart(attendedList.get(i).getAttendanceTime());
            	schedules.add(schedule);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedules);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
}
    @GetMapping("/allScheduleURL")//職員の出勤予定全て
    public String getAllSchedule() {
    	String jsonMsg = null;
    	try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            //長いからlistに格納しなおす
            List<Schedule> scheduleList = new ArrayList<>();
            scheduleList = scheduleRepository.findAll();
            //Scheduleを全部　ここからjson化
            List<CalendarBuf> schedules = new ArrayList<>();
            for(int i  = 0 ; i < scheduleList.size(); i ++) {
            	CalendarBuf buf = new CalendarBuf();
            	buf.setTitle(scheduleList.get(i).getScheduleName() +"  "+ scheduleList.get(i).getUserInfo().getUsername());
            	buf.setStart(scheduleList.get(i).getScheduleDate());
            	if(scheduleList.get(i).getScheduleName().equals("休暇") || scheduleList.get(i).getScheduleName().equals("年休")) {
            		buf.setColor("red");
            	}
            	schedules.add(buf);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedules);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
            }
    	return jsonMsg;
    }
    @GetMapping("/number")//人数と出勤、退勤を見れるように
    public String gettest(Authentication loginuser) throws ParseException {
    	String jsonMsg = null;
    	try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            //長いからlistに格納しなおす
            List<Schedule> scheduleList = new ArrayList<>();
            scheduleList = scheduleRepository.findAll();
            Map<CalendarBuf, Integer> bufList = new HashMap<>();
            for(Schedule i : scheduleList) {
            	CalendarBuf c = new CalendarBuf();
            	c.setStart(i.getScheduleDate());
            	c.setTitle(i.getScheduleName());
            	if (bufList.containsKey(c)) {
            		bufList.replace(c, (Integer)bufList.get(c) + 1);
            	}else {
            		bufList.put(c, 1);
            	}
        	}
        
            //ここからjason化
            List<CalendarBuf> schedules = new ArrayList<>();
            for(Map.Entry<CalendarBuf, Integer> i : bufList.entrySet()) {
            	CalendarBuf schedule = new CalendarBuf();
        		schedule.setTitle(i.getKey().getTitle() + "  " + i.getValue() + "人");
        		schedule.setStart(i.getKey().getStart());
        		if(i.getKey().getTitle().equals("休暇") || i.getKey().getTitle().equals("年休")) {
        			schedule.setColor("red");
            	}
        		schedules.add(schedule);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedules);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
            }
    	return jsonMsg;
    }
   
}
