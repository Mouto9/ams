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

@Getter
@Setter
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//DBのID列を使用してキーの自動採番を行う
    																					//（）内は採番をSQLとWEBで一致させるため
    private Long id;
    
    @Size(min = 2, max = 4)
    @UniqueLogin
    private String username;//職員ID

    @Size(min = 4, max = 255)
    private String password;

    
    @OneToMany(mappedBy = "userInfo",cascade=CascadeType.ALL)
    private List<Attendance> attendance;
    
    @OneToMany(mappedBy = "userInfo",cascade=CascadeType.ALL)
    private List<Schedule> schedule;
    
    private boolean admin;
    private String role;
    private boolean active = true;
}
