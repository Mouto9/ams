package com.example.demo.model;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import com.example.demo.validator.UniqueLogin;

import lombok.Getter;
import lombok.Setter;

/**
 * 職員情報テーブル。
 *
 */
@Getter
@Setter
@Entity
@UniqueLogin(fields = {"id","username"})
public class UserInfo {
    /**
     * 職員情報テーブルID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DBのID列を使用してキーの自動採番を行う
    																					//（）内は採番をSQLとWEBで一致させるため
    private Long id;
    
    /**
     * 職員ID。
     */
    @Size(min = 4, max = 4,message="4文字で入力してください")
    private String username;//職員ID
    
    /**
     * 苗字。
     */
    @Size(min = 1, max = 7,message="1文字から7文字で入力してください")
    private String lastName;//苗字
    /**
     * 氏名。
     */
    @Size(min = 1, max = 7,message="1文字から7文字で入力してください")
    private String firstName;//氏名
    
    /**
     * パスワード。
     */
    @Size(min = 8, max = 255,message="8文字以上255文字以下で入力してください")
    private String password;
    
    
    /**
     * 出勤情報。
     */
    @OneToMany(mappedBy = "userInfo",cascade=CascadeType.ALL)
    private List<Attendance> attendance;
    
    /**
     * 勤怠情報。
     */
    @OneToMany(mappedBy = "userInfo",cascade=CascadeType.ALL)
    private List<Schedule> schedule;
    
    /**
     * 権限の有無。
     */
    private boolean admin;
    /**
     * 権限。
     */
    private String role;
    /**
     * ログイン中。
     */
    private boolean active = true;
}
