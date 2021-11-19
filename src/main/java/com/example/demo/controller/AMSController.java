package com.example.demo.controller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.Attendance;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AMSController {
	private final AttendanceRepository attendRepository;
	private final UserInfoRepository userRepository;
    
    @GetMapping("/login")//ログイン
    public String login(Authentication loginuser) {
        return "login";
    }

    @GetMapping("/")		//ホーム画面
    public String showList(Authentication loginuser,Model model){
    	try {
	    	UserInfo userInfo = userRepository.findByUsername(loginuser.getName());
	    	List<Attendance> attendedInfo = attendRepository.findByUserInfo(userInfo);
	    	Date today = new Date();
	    	
	    	Calendar calendarAttended = Calendar.getInstance();
	    	calendarAttended.setTime(attendedInfo.get(attendedInfo.size() - 1).getAttendanceTime());//最新の出勤処理した時刻
	    	Calendar calendarToday = Calendar.getInstance();
	    	calendarToday.setTime(today);
	    	
	    	if(calendarAttended.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR)) {
	    		model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
	    		return "mypage";
	    	}else {
	    		return "attend";
	    	}
    	}catch(Exception e) {
    		return "attend";
    	}
    	
    }
    
    @GetMapping("/attend")//出勤処理画面
    public String attend(Authentication loginuser,Attendance attendedInfo,UserInfo userInfo,Model model) {
    	Date date = new Date();
    	attendedInfo.setAttendanceTime(date);    	 
    	
    	userInfo = userRepository.findByUsername(loginuser.getName());
    	attendedInfo.setUserInfo(userInfo);
    	
        attendRepository.save(attendedInfo);
       return "redirect:/mypage";
    }
    @GetMapping("/mypage")//自分の当日の出勤時刻、勤怠予定一覧
    public String mypage(Authentication loginuser,Model model) {
    	model.addAttribute("users",userRepository.findByUsername(loginuser.getName()));
    	return "mypage";
    }

    @GetMapping("/allSchedule")//全職員の全予定がわかるカレンダー
    public String allSchedule() {
        return "allSchedule";
    }
    //個々からテスト
    @GetMapping("/test")		//ホーム画面
    public String test(){
    		return "test";
    }
    /**
     * Object collection output demo
     * @author Leonid Vysochyn
     */
        private static Logger logger = LoggerFactory.getLogger(AMSController.class);

        @GetMapping("/demo")
        public String demo(String[] args) throws ParseException, IOException {
            logger.info("Running Object Collection demo");
            List<Attendance> attend = attendRepository.findAll();
            try(InputStream is = AMSController.class.getResourceAsStream("object_collection_template.xls")) {
                try (OutputStream os = new FileOutputStream("target/object_collection_output.xls")) {
                    Context context = new Context();
                    context.putVar("attend", attend);
                    JxlsHelper.getInstance().processTemplate(is, os, context);
                }
            }
            return "redirect:/mypage";
            
        }
}
