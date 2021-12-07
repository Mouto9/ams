package com.example.demo.controller;
import java.util.Calendar;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.Attendance;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

/**
 * 全職員用コントローラ。
 *
 */
@RequiredArgsConstructor
@Controller
public class AMSController {
	private final AttendanceRepository attendanceRepository;
	private final UserInfoRepository userRepository;
    /**
     * ログイン。
     * @param loginuser ログインユーザー。
     * @return "login"
     */
    @GetMapping("/login")//ログイン
    public String login(Authentication loginuser) {
        return "login";
    }

    /**
     * 当日に出勤時刻があれば「マイページ画面」へ、無ければ「出勤処理画面」へ遷移する。
     * @param loginuser ログインユーザー。
     * @param model モデル。
     * @param attendance 出勤情報。
     * @param userInfo 職員情報。
     *  @return "mypage" or "attend"
     */
    @GetMapping("/")
    public String mypageORattend(Authentication loginuser,Model model,Attendance attendance,UserInfo userInfo){
        	Calendar todayStart = Calendar.getInstance();
        	Calendar todayEnd = Calendar.getInstance();
        	todayStart.set(Calendar.HOUR_OF_DAY,0);
        	todayEnd.set(Calendar.HOUR_OF_DAY, 24);
        	if(attendanceRepository.existsByAttendanceTimeBetweenAndUserInfo(todayStart.getTime(),todayEnd.getTime() , userRepository.findByUsername(loginuser.getName()))) {
        		return mypage(loginuser,model);
        	}else {
        		return "attend";
        	}
    }
    
    /**
     * 出勤処理を行う。
     * @param loginuser ログインユーザー。
     * @param attendance 出勤情報。
     * @return "mypage"
     */
    @GetMapping("/attend")
    public String attend(Authentication loginuser,Attendance attendance) {
    	Date date = new Date();
    	attendance.setAttendanceTime(date);    	 
    	attendance.setUserInfo(userRepository.findByUsername(loginuser.getName()));
        attendanceRepository.save(attendance);
       return "redirect:/mypage";
    }
    /**
     * マイページ画面。
     * @param loginuser ログインユーザー。
     * @param model モデル。
     * @return "mypage"
     */
    @GetMapping("/mypage")//名前
    public String mypage(Authentication loginuser,Model model) {
    	model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
    	return "mypage";
    }

    /**
     * 全職員の全勤怠情報がわかるカレンダー
     * @return "allSchedule"
     */
    @GetMapping("/allSchedule")
    public String allSchedule() {
        return "allSchedule";
    }
}
