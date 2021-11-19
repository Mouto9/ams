package com.example.demo.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.CalendarBuf;
import com.example.demo.model.Schedule;
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
    private final AttendanceRepository attendedRepository;
    
    @GetMapping("/admin/scheduleRegister")//勤怠登録画面へ遷移	calendarbufに職員情報を入れて渡す
    public String attend(Model model,CalendarBuf calendarBuf) {//来月の予定を入れるためinput type date用に初期値を生成している
    	Date thisYear = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(thisYear);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendarBuf.setStart(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendarBuf.setEnd(calendar.getTime());
		
		calendarBuf.setUserList(userRepository.findAll());
		model.addAttribute("calendarBuf",calendarBuf);
        return "admin/scheduleRegister";
    }
    
    @PostMapping("/admin/scheduleRegister")//勤怠予定が入力されたら保存
    	public String attend(@Validated @ModelAttribute("calendarBuf") CalendarBuf calendarBuf,BindingResult result) {
    	Calendar startCalendar = Calendar.getInstance();
    	startCalendar.setTime(calendarBuf.getStart());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(calendarBuf.getEnd());
    	int start = startCalendar.get(Calendar.DAY_OF_MONTH);//予定開始日
		int end = endCalendar.get(Calendar.DAY_OF_MONTH);//予定終了日
		
		List<Long> list = new ArrayList<>();
    	list= calendarBuf.getUserInfoIdList();//userinfo.idをうけとる
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
			    		schedule.setScheduleName("休日");
			    	}else {
			    		schedule.setScheduleName("出勤");
			    	}
		    		schedule.setScheduleDate(startCalendar.getTime());
		    		
		    		scheduleRepository.save(schedule);
				}
    		}
    	}
    	
    	if (result.hasErrors()) {
            return "admin/scheduleRegister";
        }
        return "redirect:/allSchedule";
    }
    
    @GetMapping("/admin/attended/{id}")//指定IDの出勤時刻を表示
    public String attendedList(@PathVariable long id,Model model) {
    	model.addAttribute("users", userRepository.findById(id));
    	return "admin/attended";
    }
    
    @GetMapping("/admin/schedule/{id}")//指定IDの予定を表示
    public String schedule(@PathVariable long id,Model model) {
    	model.addAttribute("users", userRepository.findById(id));
    	return "admin/schedule";
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
    	attendedRepository.deleteByUserInfoId(id);
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
