package com.example.demo.controller;
import java.text.ParseException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.UserInfo;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.util.Role;

import lombok.RequiredArgsConstructor;

/**
 * 職員情報管理用コントローラ。
 *
 */
@RequiredArgsConstructor
@Controller
public class UserManageController {
    private final UserInfoRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    
    //以下職員管理
    /**
     * @param model モデル。
     * @param user 職員情報。
     * @return "admin/userManage"
     * @throws ParseException パースエラー。
     */
    @GetMapping("/admin/userManage")//職員一覧画面の出力
    public String showAdminList(Model model,UserInfo user) throws ParseException {
    	model.addAttribute("users", userRepository.findAll());
    	model.addAttribute("test", user);
    	return "admin/userManage";
    }
    
    
    /**
     * @param id 職員情報テーブルID。
     * @param model モデル。
     * @return "admin/userEdit"
     */
    @GetMapping("/admin/userEdit/{id}")//職員編集画面
    public String editUser(@PathVariable Long id, Model model) {
    	model.addAttribute("user",userRepository.findById(id));
    	return "admin/userEdit";
    }
    
    /**
     * @param user 職員情報。
     * @param result リザルト。
     * @return "admin/userEdit"
     */
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

        return "redirect:/admin/userManage";
    }
   
    /**
     * @param id 職員情報テーブルID。
     * @return "redirect:/admin/userManage"
     */
    @GetMapping("/admin/delete/{id}")//職員削除機能
    public String deleteUser(@PathVariable long id) {
    	scheduleRepository.deleteByUserInfoId(id);
    	attendanceRepository.deleteByUserInfoId(id);
    	userRepository.deleteById(id);
    	
    	return "redirect:/admin/userManage";
    }
    
    /**
     * @param user 職員情報。
     * @param result リザルト。
     * @param model モデル。
     * @return "redirect:/admin/userManage"
     * @throws ParseException パースエラー。
     */
    @PostMapping("/admin/userManage")//職員登録
    public String adminRegister(@Validated @ModelAttribute("test") UserInfo user,BindingResult result , Model model) throws ParseException {

        if (result.hasErrors()) {
            return showAdminList(model,user);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.isAdmin()) {
            user.setRole(Role.ADMIN.name());
        } else {
            user.setRole(Role.USER.name());
        }
        userRepository.save(user);

        return "redirect:/admin/userManage";
    }
    
    
}
