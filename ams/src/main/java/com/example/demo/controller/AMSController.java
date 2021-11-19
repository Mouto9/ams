package com.example.demo.controller;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Attendance;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AMSController {
	private final AttendanceRepository attendedRepository;
	private final UserInfoRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	//private final ScheduleMapper scheduleMapper;
    
    @GetMapping("/login")//ログイン
    public String login(Authentication loginuser) {
        return "login";
    }

    @GetMapping("/")		//ホーム画面
    public String showList(Authentication loginuser,Model model){
    	try {
	    	UserInfo userInfo = userRepository.findByUsername(loginuser.getName());
	    	List<Attendance> attendedInfo = attendedRepository.findByUserInfo(userInfo);
	    	Date today = new Date();
	    	
	    	Calendar calendarAttended = Calendar.getInstance();
	    	calendarAttended.setTime(attendedInfo.get(attendedInfo.size() - 1).getAttendanceTime());//最新の出勤処理した時刻
	    	Calendar calendarToday = Calendar.getInstance();
	    	calendarToday.setTime(today);
	    	
	    	if(calendarAttended.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR)) {
	    		model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
	    		return "mypage";
	    	}else {
	    		return "user";
	    	}
    	}catch(Exception e) {
    		return "user";
    	}
    	
    }
    
    @GetMapping("/attended")//出勤処理画面
    public String attended(Authentication loginuser,
    								Attendance attendedInfo,
    								UserInfo userInfo,
    								Model model) {
    	Date date = new Date();
    	attendedInfo.setAttendanceTime(date);    	 
    	
    	userInfo = userRepository.findByUsername(loginuser.getName());
    	attendedInfo.setUserInfo(userInfo);
    	
        attendedRepository.save(attendedInfo);
       return "redirect:/mypage";
    }
    @GetMapping("/mypage")//自分の当日の出勤時刻、勤怠予定一覧
    public String mypage(Authentication loginuser,Model model) {
    	model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
    	return "mypage";
    }
    @GetMapping("/attendedInfo")//自分の過去の押下時間一覧
    public String attendedInfo(Authentication loginuser,Model model) {
    	model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
    	return "attendedInfo";
    }
    //ここまで
    
    @GetMapping("/register")
    public String register(@ModelAttribute("user") UserInfo user) {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") UserInfo user,BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/login?register";
    }
    @GetMapping("/allSchedule")//全職員の全予定がわかるカレンダー
    public String print() {
        return "allSchedule";
    }
    //個々からテスト
    @GetMapping("/test")		//ホーム画面
    public String test(){
    		return "test";
    }
}
