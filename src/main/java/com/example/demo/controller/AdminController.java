package com.example.demo.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
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

import lombok.RequiredArgsConstructor;

/**
 * 管理者用コントローラクラス。
 */
@RequiredArgsConstructor
@Controller
public class AdminController {
	
    private final UserInfoRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    
    /**
     * @return "admin/allSchedule"
     */
    @GetMapping("/admin/allSchedule")//全職員の全予定がわかるカレンダー
    public String allSchedule() {
        return "admin/allSchedule";
    }
    
    /**
     * @param model モデル。
     * @param sr バッファ。
     * @return "admin/scheduleRegister"
     * @throws IOException IOエラー。
     * @throws ParseException パースエラー。
     */
    @GetMapping("/admin/scheduleRegister")//勤怠登録画面へ遷移	calendarbufに職員情報を入れて渡す
    public String scheduleRegister(Model model,ScheduleRegister sr) throws IOException, ParseException {//来月の予定を入れるためinput type date用に初期値を生成している
    	
    	Date thisYear = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(thisYear);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		sr.setStart(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		sr.setEnd(calendar.getTime());
		
		sr.setUserList(userRepository.findAllByOrderByUsername());
		model.addAttribute("calendarBuf",sr);
		
        return "admin/scheduleRegister";
    }
    
    /**
     * @param sr バッファ。
     * @param result リザルト。
     * @param model モデル。
     * @return "redirect:/admin/allSchedule"
     * @throws IOException IOエラー。
     * @throws ParseException パースエラー。
     */
    @PostMapping("/admin/scheduleRegister")//勤怠予定が入力されたら保存
    	public String scheduleRegister(@Validated @ModelAttribute("calendarBuf") ScheduleRegister sr,BindingResult result,Model model) throws IOException, ParseException {
    	if (result.hasErrors()) {
    		return scheduleRegister(model,sr);
    	}
    	//祝日を格納
    	URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
        BufferedReader in = new BufferedReader(
    	                new InputStreamReader(
    	                url.openStream()));
    	String inputLine;
    	List<String> bufList = new ArrayList<>();
    	while ((inputLine = in.readLine()) != null) {
    		if(inputLine.equals("{") || inputLine.equals("}")) {
    			continue;
    		}
    		bufList.add(inputLine.substring(5,15));
    	}
    	//ここまで
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
			    	if(startCalendar.get(Calendar.DAY_OF_WEEK) == 7 ){
			    		schedule.setScheduleName("土曜");
			    	}else if(startCalendar.get(Calendar.DAY_OF_WEEK)== 1) {
			    		schedule.setScheduleName("日曜");
			    	}else if(bufList.contains(sdf.format(startCalendar.getTime()))){
			    		schedule.setScheduleName("休日");
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
    
    /**
     * @param id 職員情報テーブルID。
     * @param model モデル。
     * @return "admin/scheduleList"
     */
    @GetMapping("/admin/scheduleList/{id}")//指定IDの勤怠情報を表示
    public String scheduleList(@PathVariable long id, Model model) {
    	model.addAttribute("user", userRepository.findById(id));
    	return "admin/scheduleList";
    }
    
    /**
     * @param id 勤怠情報テーブルID。
     * @param model モデル。
     * @param buf バッファ。
     * @param sr バッファ。
     * @return "admin/scheduleEdit"
     */
    @GetMapping("/admin/scheduleEdit/{id}")
    public String saveSchedule(@PathVariable long id, Model model,CalendarBuf buf,ScheduleRegister sr) {
    	 model.addAttribute("schedule",scheduleRepository.findById(id));
    	Map<String, String> sName = new LinkedHashMap<String, String>();
    	sName.put("001", "出勤");
        sName.put("002", "休日");
        sName.put("003", "年休");
        sName.put("004", "特休");
        sName.put("005", "テレ");
        sName.put("006","夏休");
        sName.put("007","出張");
        sName.put("008","時休");
        model.addAttribute("sName",sName);
        buf.setTitle("08:30");
        model.addAttribute("jikyu",buf);
        
        Map<String, String> hou = new LinkedHashMap<String, String>();
    	hou.put("001", "1");
        hou.put("002", "2");
        hou.put("003", "3");
        hou.put("004", "4");
        hou.put("005", "5");
        hou.put("006","6");
        hou.put("007","7");
        model.addAttribute("hou",hou);
        
        List<String> t = new ArrayList<>(Arrays.asList("1限目","2限目","3限目","4限目","5限目" )) ;
        sr.setTereGive(t);
        
        model.addAttribute("sr",sr);
          return "admin/scheduleEdit";
     }
    
    /**
     * @param schedule 勤怠情報。
     * @param buf バッファ。
     * @param result リザルト。
     * @param sr バッファ。
     * @return "redirect:/admin/scheduleList/(職員情報テーブルID)"
     * @throws ParseException パースエラー。
     */
    @PostMapping("/admin/scheduleEdit")
   public String editSchedule(@Validated @ModelAttribute("schedule") Schedule schedule,CalendarBuf buf,BindingResult result,
		   @ModelAttribute("sr") ScheduleRegister sr) throws ParseException {

    	if(schedule.getScheduleName().equals("テレ")) {
    		int count = 0;
    		String start = "";
        	for(String s:sr.getTereServe()) {
        		switch(s) {
        		case "1限目":
        			start += "08:301限目  ";
        			count += 120;
        			break;
        		case "2限目":
        			start += "2限目  ";
        			count += 105;
        			break;
        		case "3限目":
        			start += "3限目  ";
        			count += 105;
        			break;
        		case "4限目":
        			start += "4限目  ";
        			count += 105;
        			break;
        		case "5限目":
        			start += "5限目  ";
        			count += 30;
        			break;
        			default:
        			
        		}
        	}
        	String tmp;
        	if(count == 465) {
        		tmp = "1日";
        		start = "08:301日";
        	}else {
        		if(count % 60 == 0) {
        			tmp = Integer.toString(count / 60);
        		}else {
        			tmp = (count / 60) + ":" + (count % 60);
        		}
        	}
        	schedule.setScheduleStart(start);
    		schedule.setScheduleTime(tmp);
    	}else if(schedule.getScheduleName().equals("時休")) {
    		schedule.setScheduleTime(buf.getColor().substring(0,1));
    		schedule.setScheduleStart(buf.getTitle());
    	}
    	
           if (result.hasErrors()) {
              return "admin/allSchedule";
           } else {
              scheduleRepository.save(schedule);
     
              return  "redirect:/admin/scheduleList/" + schedule.getUserInfo().getId();
       }
     }
    
    /**
     * @param id 勤怠情報テーブルID。
     * @return "redirect:/admin/scheduleList/(職員情報テーブルID)"
     */
    @GetMapping({"/admin/scheduleDelete/{id}"})   
    public String deleteSchedule(@PathVariable long id) {
    	      long tmp = scheduleRepository.findById(id).getUserInfo().getId();
    	      scheduleRepository.deleteById(id);
    	     
         return "redirect:/admin/scheduleList/" + tmp;
    }
    /**
     * @param model モデル。
     * @param sr バッファ。
     * @return "admin/attendanceManage"
     * @throws ParseException パースエラー。
     */
    @GetMapping("/admin/attendanceManage")//勤怠管理画面の出力
    public String attendanceManage(Model model,ScheduleRegister sr) throws ParseException {
    	model.addAttribute("users", userRepository.findAll());
    	
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
    	return "admin/attendanceManage";
    }

    
    /**
     * @param sr バッファ。
     * @param result リザルト。
     * @param model モデル。
     * @return "redirect:/admin/allSchedule"
     * @throws ParseException パースエラー。
     */
    @PostMapping({"/admin/attendanceManage"})   
    public String deleteScheduleList(ScheduleRegister sr,BindingResult result,Model model) throws ParseException {

    	if (result.hasErrors()) {
    		return attendanceManage(model,sr);
    	}
    	
    	Calendar startCalendar = Calendar.getInstance();
    	startCalendar.setTime(sr.getStart());
    	Calendar endCalendar = Calendar.getInstance();
    	endCalendar.setTime(sr.getEnd());
    	int start = startCalendar.get(Calendar.DAY_OF_MONTH);//削除開始日
		int end = endCalendar.get(Calendar.DAY_OF_MONTH);//削除終了日
		
		List<Long> list = new ArrayList<>();
    	list= sr.getUserInfoIdList();//userinfo.idをうけとる

    	for(int i = start; i <= end;i++ ) {											//forで予定開始日から終了日まで繰り返し
			startCalendar.set(Calendar.DAY_OF_MONTH, i);
    		
    		for(Long j : list) {
    			scheduleRepository.deleteByUserInfoAndScheduleDate(userRepository.findById((long)j), startCalendar.getTime());
    		}
		}
    	
    	     return  "redirect:/admin/allSchedule";
    }
    
    /**
     * @param id 職員情報テーブルID。
     * @param model モデル。
     * @return "admin/attendList"
     */
    @GetMapping("/admin/attendList/{id}")//指定IDの予定を表示
    public String attendedList(@PathVariable long id, Model model) {
    	model.addAttribute("users", userRepository.findById(id));
    	return "admin/attendList";
    }
    /**
     * @param id 職員情報テーブルID。
     * @param model モデル。
     * @param buf バッファ。
     * @return "admin/attendRegister"
     */
    @GetMapping("/admin/attendRegister/{id}")
    public String attendRegister(@PathVariable long id, Model model, CalendarBuf buf) {
    	Date date = new Date();
    	SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
    	buf.setAttend(hms.format(date));
    	SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
    	buf.setTitle(hm.format(date));
    	buf.setUser(userRepository.findById(id));
    	model.addAttribute("buf", buf);
    	return "admin/attendRegister";
    }
    /**
     * @param buf バッファ。
     * @param result バッファ。
     * @param attendance 出勤情報。
     * @param model モデル。
     * @return "redirect:/admin/attendList/(職員情報テーブルID)"
     * @throws ParseException パースエラー。
     */
    @PostMapping("/admin/attendRegister")
    public String saveAttendRegister(@Validated @ModelAttribute("buf") CalendarBuf buf,BindingResult result,Attendance attendance , Model model) throws ParseException {
    	if (result.hasErrors()) {
    		return  attendRegister(buf.getUser().getId(), model, buf) ;
    	}
    	attendance.setUserInfo(buf.getUser());
    	
    	String d = buf.getAttend() + " " +buf.getTitle();
    	SimpleDateFormat dfset = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  	  attendance.setAttendanceTime(dfset.parse(d));
  	attendanceRepository.save(attendance);
    	long tmp = buf.getUser().getId();
    	return "redirect:/admin/attendList/" + tmp;
    }
    /**
     * @param id 出勤情報テーブルID。
     * @param model モデル。
     * @param buf バッファ。
     * @return "admin/attendEdit"
     */
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
    
  /**
 * @param buf バッファ。
 * @param attendance 出勤情報。
 * @return "redirect:/admin/attendList/(職員情報テーブルID)";
 * @throws ParseException パースエラー。
 */
@PostMapping("/admin/attendEdit")
   public String editAttend(@ModelAttribute("attend") CalendarBuf buf,Attendance attendance) throws ParseException {
	  attendance.setId(buf.getId());
	  attendance.setUserInfo(buf.getUser());
	  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	  String d = df.format(buf.getStart()) + " " +buf.getTitle();
	  SimpleDateFormat dfset = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  attendance.setAttendanceTime(dfset.parse(d));
	  long tmp = buf.getUser().getId();

            attendanceRepository.save(attendance);
            return "redirect:/admin/attendList/" + tmp;
    }
   /**
 * @param id 出勤情報テーブルID。
 * @return "redirect:/admin/attendList/(出勤情報テーブルID)"
 */
@GetMapping("/admin/attendDelete/{id}")
   public String deleteAttend(@PathVariable long id) {
           long tmp = this.attendanceRepository.findById(id).getUserInfo().getId();
          this.attendanceRepository.deleteById(id);
    
         return "redirect:/admin/attendList/" + tmp;
    }
    /**
     * @param loginUser ログインユーザー。
     * @param model モデル。
     * @return "attendancedook"
     */
    @GetMapping("/admin/attendancebook")
    public String showAttendanceBook(Authentication loginUser, Model model) {
    	changeAttendanceBook(userRepository.findByUsername(loginUser.getName()).getId(), Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), Calendar.getInstance().get(Calendar.MONTH) > 6 ? "後期" : "前期", model);
    	
    	return "attendancedook";
    }
    
    /**
     * @param userId 職員情報テーブルID。
     * @param selectedyear 選択した年。
     * @param selectedperiod 前期又は後期。
     * @param model モデル。
     * @return　"attendancedook::main-content"
     */
    @GetMapping("/admin/attendancebook/{selecteduserid}/{selectedyear}/{selectedperiod}")
    public String changeAttendanceBook(@PathVariable("selecteduserid") long userId, @PathVariable("selectedyear") String selectedyear, @PathVariable("selectedperiod") String selectedperiod, Model model){
    	Set<String> attendanceSet = new HashSet<>();
    	Set<String> yearsSet = new HashSet<>();
		List<String> scheduleType = new ArrayList<>(Arrays.asList("年休", "特休", "夏休", "出張" ,"休日","土曜","日曜" )) ;
    	Map<String,String> scheduleMap = new HashMap<>();
    	Map<String,String> scheduleTimeMap = new HashMap<>();
    	List<Schedule> scheduleList = new ArrayList<>();
    	
    	UserInfo userInfo = userRepository.findById(userId);
    	scheduleList= scheduleRepository.findByUserInfo(userInfo);
    	
    	attendanceRepository.findByUserInfo(userInfo).stream().forEach(a -> attendanceSet.add(new SimpleDateFormat("yyyyMMdd").format(a.getAttendanceTime())));	//出勤情報の年月日を格納
    	scheduleList.stream().forEach(a -> yearsSet.add(new SimpleDateFormat("yyyy").format(a.getScheduleDate())));	//年の選択候補を格納（降順
    	scheduleList.stream()
							.filter(f -> scheduleType.contains(f.getScheduleName()))
							.forEach(f -> {
								scheduleMap.put(new SimpleDateFormat("yyyyMMdd").format(f.getScheduleDate()), f.getScheduleName());
							});  
    	scheduleRepository.findByUserInfoAndScheduleTimeIsNotNull(userInfo).stream()
																.filter(f -> f.getScheduleName().equals("時休") || f.getScheduleName().equals("テレ") )
																.forEach(f -> {
																	if(f.getScheduleName().equals("時休")) {
																		scheduleTimeMap.put(new SimpleDateFormat("yyyyMMdd").format(f.getScheduleDate()), "ネ - " + f.getScheduleTime());
																	}else if(f.getScheduleName().equals("テレ") && !f.getScheduleTime().equals("1日")) {
																		scheduleTimeMap.put(new SimpleDateFormat("yyyyMMdd").format(f.getScheduleDate()), f.getScheduleTime() + " - テ");
																	}else if(f.getScheduleName().equals("テレ") && f.getScheduleTime().equals("1日")) {
																		scheduleTimeMap.put(new SimpleDateFormat("yyyyMMdd").format(f.getScheduleDate()), "　");
																	}
																	
																	if(f.getScheduleStart().substring(0,5).equals("08:30")){
																		scheduleMap.put(new SimpleDateFormat("yyyyMMdd").format(f.getScheduleDate()), f.getScheduleName());
																	}
																});  
    	
        model.addAttribute("userlist", userRepository.findAllByOrderByUsername());
        model.addAttribute("selecteduser", userInfo);
    	model.addAttribute("selectyearlist", yearsSet.stream().sorted(Comparator.reverseOrder()).toArray());					//年の選択候補（降順）
    	model.addAttribute("selectedyear", selectedyear);																		//選択した年（デフォルトは今年）
    	model.addAttribute("selectedperiod", selectedperiod.equals("後期") ? "後期" : "前期");						              //期間の選択候補（デフォルトは今の期間）
    	model.addAttribute("scopeofmonth", selectedperiod.equals("後期") ? new String[]{"7","12"} : new String[]{"1","6"});     //期間の数値設定
    	model.addAttribute("attendanceSet",attendanceSet);																	    //出勤情報の年月日のみ渡す
    	model.addAttribute("scheduleMap",scheduleMap);																	    //休みの情報の年月日のみ渡す
    	model.addAttribute("scheduleTimeMap",scheduleTimeMap);																 //時休の情報の年月日のみ渡す
    	
    	return "attendancedook::main-content";
    }
}
