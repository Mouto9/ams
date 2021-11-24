package com.example.demo.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Attendance;
import com.example.demo.model.CalendarBuf;
import com.example.demo.model.Schedule;
import com.example.demo.model.ScheduleRegister;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AdminController {
    private final UserInfoRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    
    @GetMapping("/admin/allSchedule")//全職員の全予定がわかるカレンダー
    public String allSchedule() {
        return "admin/allSchedule";
    }
    
    @GetMapping("/admin/scheduleRegister")//勤怠登録画面へ遷移	calendarbufに職員情報を入れて渡す
    public String scheduleRegister(Model model,ScheduleRegister sr) {//来月の予定を入れるためinput type date用に初期値を生成している
    	Date thisYear = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(thisYear);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		sr.setStart(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		sr.setEnd(calendar.getTime());
		
		sr.setUserList(userRepository.findAllByOrderByUsername());
		model.addAttribute("calendarBuf",sr);
        return "admin/scheduleRegister";
    }
    
    @PostMapping("/admin/scheduleRegister")//勤怠予定が入力されたら保存
    	public String scheduleRegister(@Validated @ModelAttribute("calendarBuf") ScheduleRegister sr,BindingResult result,Model model) {
    	
    	if (result.hasErrors()) {
    		return scheduleRegister(model,sr);
    	}
    	
    	Calendar startCalendar = Calendar.getInstance();
    	startCalendar.setTime(sr.getStart());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(sr.getEnd());
    	int start = startCalendar.get(Calendar.DAY_OF_MONTH);//予定開始日
		int end = endCalendar.get(Calendar.DAY_OF_MONTH);//予定終了日
		
		List<Long> list = new ArrayList<>();
    	list= sr.getUserInfoIdList();//userinfo.idをうけとる
    	//ここから日付と出勤か休みかと職員リストを格納する
    	for(Long j : list) {//選択した職員
    		List<Schedule>scheduleList = new ArrayList<>();
    		//指定職員IDのスケジュールを全部取得してその中から日付情報をdateListに格納
    		scheduleList = scheduleRepository.findByUserInfo(userRepository.findById((long)j));
    		List<Date>dateList = new ArrayList<>();
    		for(Schedule d: scheduleList) {
    			dateList.add(d.getScheduleDate());
    		}
    		if(dateList.contains(startCalendar.getTime())){	
    		}else {
				for(int i = start; i <= end;i++ ) {											//forで予定開始日から終了日まで繰り返し格納
					Schedule schedule = new Schedule();
					schedule.setUserInfo(userRepository.findById((long)j));
					startCalendar.set(Calendar.DAY_OF_MONTH, i);
		    		//土日を探す	scheduleName
			    	if(startCalendar.get(Calendar.DAY_OF_WEEK) == 1 || startCalendar.get(Calendar.DAY_OF_WEEK)== 7){
			    		schedule.setScheduleName("休暇");
			    	}else {
			    		schedule.setScheduleName("出勤");
			    	}
		    		schedule.setScheduleDate(startCalendar.getTime());
		    		
		    		scheduleRepository.save(schedule);
				}
    		}
    	}
    	
        return "redirect:/admin/allSchedule";
    }
    
    @GetMapping("/admin/scheduleList/{id}")//指定IDの勤怠情報を表示
    public String scheduleList(@PathVariable long id, Model model) {
    	model.addAttribute("user", userRepository.findById(id));
    	return "admin/scheduleList";
    }
    
    @GetMapping("/admin/scheduleEdit/{id}")
    public String saveSchedule(@PathVariable long id, Model model) {
    	 model.addAttribute("schedule",scheduleRepository.findById(id));
    	Map<String, String> sName = new LinkedHashMap<String, String>();
    	sName.put("001", "出勤");
        sName.put("002", "休暇");
        sName.put("003", "年休");
        sName.put("004", "特休");
        sName.put("005", "テレ");
        sName.put("006","夏休");
        sName.put("007","出張");
        model.addAttribute("sName",sName);
           model.addAttribute("default","002");
          return "admin/scheduleEdit";
     }
    
    @PostMapping("/admin/scheduleEdit")
   public String editSchedule(@Validated @ModelAttribute("schedule") Schedule schedule, BindingResult result) {
    	long tmp = schedule.getUserInfo().getId();
           if (result.hasErrors()) {
              return "admin/allSchedule";
           } else {
              scheduleRepository.save(schedule);
     
              String url = "redirect:/admin/scheduleList/" + tmp;
              return url;
       }
     }
    
    @GetMapping({"/admin/scheduleDelete/{id}"})   
    public String deleteSchedule(@PathVariable long id) {
    	      long tmp = scheduleRepository.findById(id).getUserInfo().getId();
    	      scheduleRepository.deleteById(id);
    	     String url = "redirect:/admin/scheduleList/" + tmp;
    	     
         return url;
    }
    
    @GetMapping("/admin/attendList/{id}")//指定IDの予定を表示
    public String attendedList(@PathVariable long id, Model model) {
    	model.addAttribute("users", userRepository.findById(id));
    	return "admin/attendList";
    }
    
    @GetMapping("/admin/attendEdit/{id}")//CalendarBufで
   public String saveAttend(@PathVariable long id, Model model,CalendarBuf buf) {
    	Attendance attendance = attendanceRepository.findById(id);
    	SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    	String st = df.format(attendance.getAttendanceTime());
    	buf.setTitle(st);
    	buf.setStart(attendance.getAttendanceTime());
    	buf.setId(attendance.getId());
    	buf.setUser(attendance.getUserInfo());
    	model.addAttribute("attend",buf);
          return "admin/attendEdit";
    }
    
  @PostMapping("/admin/attendEdit")
   public String editAttend(@Validated @ModelAttribute("attend") CalendarBuf buf,Attendance attendance, BindingResult result) throws ParseException {
	  attendance.setId(buf.getId());
	  attendance.setUserInfo(buf.getUser());
	  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	  String d = df.format(buf.getStart()) + " " +buf.getTitle();
	  SimpleDateFormat dfset = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  attendance.setAttendanceTime(dfset.parse(d));
	  long tmp = buf.getUser().getId();
          if (result.hasErrors()) {
              return "admin/allSchedule";
          }
            attendanceRepository.save(attendance);
            String url = "redirect:/admin/attendList/" + tmp;
            return url;
    }
   @GetMapping("/admin/attendDelete/{id}")
   public String deleteAttend(@PathVariable long id) {
           long tmp = this.attendanceRepository.findById(id).getUserInfo().getId();
          this.attendanceRepository.deleteById(id);
    
         String url = "redirect:/admin/attendList/" + tmp;
         return url;
    }
    
    //以下職員管理
    @GetMapping("/admin/userList")//職員一覧画面の出力
    public String showAdminList(Model model) throws ParseException {
    	model.addAttribute("users", userRepository.findAll());
    	Date date = new Date();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	String st = df.format(date);
    	Date sc = df.parse(st);
    	System.out.println();
    	model.addAttribute("today",scheduleRepository.findByScheduleDate(sc));
    	
    	st = st + " 23:59";
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	Date en = sdf.parse(st);
    	model.addAttribute("attend",attendanceRepository.findByAttendanceTimeBetween(sc,en));
    	return "admin/userList";
    }
    
    @GetMapping("/admin/userEdit/{id}")//職員編集画面
    public String editUser(@PathVariable Long id, Model model) {
    	model.addAttribute("user",userRepository.findById(id));
    	return "admin/userEdit";
    }
    
    @PostMapping("/admin/userEdit")
    public String editUser(@Validated @ModelAttribute("user") UserInfo user,BindingResult result) {

        if (result.hasErrors()) {
            return "admin/userEdit";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/admin/userList";
    }
   
    @GetMapping("/admin/delete/{id}")//職員削除機能
    public String deleteUser(@PathVariable long id) {
    	scheduleRepository.deleteByUserInfoId(id);
    	attendanceRepository.deleteByUserInfoId(id);
    	userRepository.deleteById(id);
    	
    	return "redirect:/admin/userList";
    }
    
    @GetMapping("/admin/userRegister")//職員登録画面
    public String adminRegister(@ModelAttribute("user") UserInfo user) {
        return "admin/userRegister";
    }
    
    @PostMapping("/admin/userRegister")//職員登録
    public String adminRegister(@Validated @ModelAttribute("user") UserInfo user,BindingResult result) {

        if (result.hasErrors()) {
            return "admin/userRegister";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/admin/userList";
    }
}
