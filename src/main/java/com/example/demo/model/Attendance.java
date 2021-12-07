package com.example.demo.model;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
/**
 * 出勤情報テーブル。
 */
@Getter
@Setter
@Entity
public class Attendance {
    /**
     * 出勤情報テーブルID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 職員情報。
     */
    @ManyToOne
    private UserInfo userInfo;
    
    /**
     * 出勤情報。
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date attendanceTime;
    
    
    
    
    	
    	
    
}
