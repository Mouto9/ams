package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Attendance;
import com.example.demo.model.CalendarBuf;
import com.example.demo.model.Schedule;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
/**
 * カレンダー用コントローラ。
 * 全てのカレンダー用のデータを作る。
 */
@RequiredArgsConstructor
@RestController
public class CalendarController {
	private final ScheduleRepository scheduleRepository;
	private final AttendanceRepository attendanceRepository;
	private final UserInfoRepository userRepository;
    /**
     * マイページ用。
     * @param loginuser ログインユーザー。
     * @return ログインユーザーの勤怠情報及び出勤情報。
     */
    @GetMapping("/myScheduleURL")
    public String getMySchedule(Authentication loginuser) {
        String jsonMsg = null;
        try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            DateFormat attendedFormat = new SimpleDateFormat("HH:mm");
            
            List<CalendarBuf> bufList = new ArrayList<>();//jsonに突っ込むリスト
            
            List<Attendance> attendList = new ArrayList<>();
            attendList = attendanceRepository.findByUserInfo(userRepository.findByUsername(loginuser.getName()));
            
            for(Attendance a : attendList) {//出勤時刻をjasonに記述する
            	CalendarBuf buf = new CalendarBuf();
            	buf.setTitle("出勤  " + attendedFormat.format(a.getAttendanceTime()));
            	buf.setStart(a.getAttendanceTime());
            	buf.setColor("#a9a9a9");
            	
            	bufList.add(buf);
            }

            List<Schedule>scheduleList = new ArrayList<>();
            scheduleList = scheduleRepository.findByUserInfo(userRepository.findByUsername(loginuser.getName()));
            for(Schedule s : scheduleList) {//勤怠予定をjasonに記述する
            	Calendar c = Calendar.getInstance();
            	c.setTime(s.getScheduleDate());
            	c.add(Calendar.DAY_OF_YEAR,1);
            	if(attendanceRepository.existsByAttendanceTimeBetweenAndUserInfo(s.getScheduleDate(),c.getTime(),s.getUserInfo())) {//もし出勤時刻があれば予定をjsonに書かない
            		continue;
            	}
            	
            	CalendarBuf buf = new CalendarBuf();
            	buf.setStart(s.getScheduleDate());
            	if(s.getScheduleName().equals("休暇") || s.getScheduleName().equals("年休") || s.getScheduleName().equals("休日")) {
            		buf.setTitle(s.getScheduleName());
            		buf.setColor("#F9ac9e");
            	}else if(s.getScheduleName().equals("土曜")|| s.getScheduleName().equals("日曜")) {
            		buf.setTitle("休日");
            		buf.setColor("#F9ac9e");
            	}else {
            		buf.setTitle(s.getScheduleName());
            	}
            	bufList.add(buf);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bufList);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        }
        return jsonMsg;
}
    /**
     * 勤怠情報用
     * @param start 開始日。
     * @param end 終了日。
     * @return 全職員の全勤怠情報
     * @throws ParseException パースエラー。
     */
    @GetMapping("/allScheduleURL")
    public String getAllSchedule(@RequestParam("start") String start,@RequestParam("end") String end) throws ParseException {
    	String jsonMsg = null;
    	try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            //長いからlistに格納しなおす
            List<Schedule> scheduleList = getList(start,end);//職員ID順
            //Scheduleを全部　ここからjson化
            List<CalendarBuf> bufList = new ArrayList<>();
            for(Schedule s : scheduleList) {
            	CalendarBuf buf = new CalendarBuf();
            	if(attendanceRepository.existsByAttendanceTimeBetweenAndUserInfo(s.getScheduleDate(),addCalendar(s.getScheduleDate()),s.getUserInfo())) {
            		buf.setTitle("出済" +"  "+ s.getUserInfo().getLastName() + " " + s.getUserInfo().getFirstName());
            	}else {
            		buf.setTitle(s.getScheduleName() +"  "+ s.getUserInfo().getLastName() + " " + s.getUserInfo().getFirstName());
            	}
            	if(s.getScheduleName().equals("休暇") || s.getScheduleName().equals("年休") || s.getScheduleName().equals("時休")) {
            		buf.setColor("#378006");
            	}
            	buf.setStart(s.getScheduleDate());
            	bufList.add(buf);
            	
            }

            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bufList);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
            }
    	return jsonMsg;
    }

    /**
     * 勤怠情報用。
     * @param start 開始日。
     * @param end 終了日。
     * @return 出済の人数。
     * @throws ParseException パースエラー。
     */
    @GetMapping("/number")//人数と出勤を見れるように
    public String getAttendedSum(@RequestParam("start") String start,@RequestParam("end") String end) throws ParseException {
    	String jsonMsg = null;
    	try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            //長いからlistに格納しなおす
            List<Schedule> scheduleList = new ArrayList<>();
            getList(start,end).stream()
            .filter(i -> attendanceRepository.existsByAttendanceTimeBetweenAndUserInfo(i.getScheduleDate(),addCalendar(i.getScheduleDate()),i.getUserInfo()))
            .forEach(i -> scheduleList.add(i));
            
            Map<CalendarBuf, Integer> map = new HashMap<>();

            for(Schedule s : scheduleList) {
            	CalendarBuf buf = new CalendarBuf();
            	buf.setStart(s.getScheduleDate());
            	buf.setTitle("出済");
            	
            	if (map.containsKey(buf)) {
            		map.replace(buf, (Integer)map.get(buf) + 1);
            	}else {
            		map.put(buf, 1);
            	}
        	}
        
            //ここからjason化
            List<CalendarBuf> schedules = new ArrayList<>();
            for(Map.Entry<CalendarBuf, Integer> i : map.entrySet()) {
            	CalendarBuf schedule = new CalendarBuf();
        		schedule.setTitle(i.getKey().getTitle() + "  " + i.getValue() + "人");
        		schedule.setStart(i.getKey().getStart());
        		schedules.add(schedule);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedules);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
            }
    	return jsonMsg;
    }
    /**
 	* 勤怠情報用。
     * @param start 開始日。
     * @param end 終了日。
     * @return 未出及び勤怠情報の人数。
     * @throws ParseException パースエラー。
     */
    @GetMapping("/notAttend")
    public String getNotAttendSum(@RequestParam("start") String start,@RequestParam("end") String end) throws ParseException {
    	String jsonMsg = null;
    	try {
            // FullCalendarにエンコード済み文字列を渡す
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            mapper.setDateFormat(df);
            //長いからlistに格納しなおす
            List<Schedule> scheduleList = new ArrayList<>();
            getList(start,end).stream()
            .filter(i -> !attendanceRepository.existsByAttendanceTimeBetweenAndUserInfo(i.getScheduleDate(),addCalendar(i.getScheduleDate()),i.getUserInfo()))
            .forEach(i -> scheduleList.add(i));
            
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
        		schedule.setStart(i.getKey().getStart());
        		if(i.getKey().getTitle().equals("休暇") || i.getKey().getTitle().equals("年休") || i.getKey().getTitle().equals("休日")) {
        			schedule.setTitle(i.getKey().getTitle() + "  " + i.getValue() + "人");
        			schedule.setColor("#F9ac9e");
        		}else if(i.getKey().getTitle().equals("土曜") || i.getKey().getTitle().equals("日曜")){
        			schedule.setTitle("休日" + "  " + i.getValue() + "人");
        			schedule.setColor("#F9ac9e");
            	}else {
        			schedule.setTitle(i.getKey().getTitle() + "  " + i.getValue() + "人");
            		schedule.setColor("silver");
            	}
        		schedules.add(schedule);
            }
            jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedules);
            } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
            }
    	return jsonMsg;
    }
    /**
     * 全てのカレンダーで利用される祝日を提供する。
     * @return 祝日。
     * @throws ParseException パースエラー。
     * @throws IOException IOエラー。
     */
    @GetMapping("/holiday")//祝日
    public String getholiday() throws ParseException, IOException {
	URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
    BufferedReader in = new BufferedReader(
	                new InputStreamReader(
	                url.openStream()));
	
	String inputLine;
	
	String jsonMsg = null;
	ObjectMapper mapper = new ObjectMapper();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    mapper.setDateFormat(df);
    
	List<CalendarBuf> bufList = new ArrayList<>();//jsonに突っ込むリスト
	int year = 0;
	while ((inputLine = in.readLine()) != null) {
		if(inputLine.equals("{") || inputLine.equals("}")) {
			continue;
		}
		CalendarBuf buf = new CalendarBuf();
		if(inputLine.substring(inputLine.length() - 1).equals(",")){
			buf.setTitle(inputLine.substring(19,inputLine.length() - 2));
		}else {
			buf.setTitle(inputLine.substring(19,inputLine.length() - 1));
			
		}
		buf.setStart(df.parse(inputLine.substring(5,15)));
		year = Integer.parseInt(inputLine.substring(5,9));
		buf.setColor("#F9ac9e");
		bufList.add(buf);
	}
	for(int i = 0; i < 3; i++) {
		bufList.add(json("休日",df.parse(year - i + "-01-02"),"#F9ac9e"));
		bufList.add(json("休日",df.parse(year - i + "-01-03"),"#F9ac9e"));
		bufList.add(json("休日",df.parse(year - i + "-12-29"),"#F9ac9e"));
		bufList.add(json("休日",df.parse(year - i + "-12-30"),"#F9ac9e"));
		bufList.add(json("休日",df.parse(year - i + "-12-31"),"#F9ac9e"));
	}
    jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bufList);
	in.close();
	
	return jsonMsg;
	}
    private CalendarBuf json(String title , Date start,String color) {
    	CalendarBuf buf = new CalendarBuf();
    	buf.setTitle(title);
    	buf.setStart(start);
    	buf.setColor(color);
    	return buf;
    }

    private Date addCalendar(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.DAY_OF_YEAR, 1);
    	return calendar.getTime();
    }
    private List<Schedule> getList(String start , String end) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    	return scheduleRepository.findByScheduleDateBetweenOrderByUserInfoUsername(sdf.parse(start), sdf.parse(end));
    }
 
}
