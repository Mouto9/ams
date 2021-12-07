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
 * 勤怠情報テーブル。
 */
@Getter
@Setter
@Entity
public class Schedule {
    /**
     * 勤怠情報ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 日付。
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date scheduleDate;
    
    /**
     * 開始時間。
     * 時休のときは開始時間。テレのときは時限数。
     */
    private String scheduleStart;//時休の開始時間
    /**
     * 期間。
     * 時休のときは時単位。テレは1日のとき。
     */
    private String scheduleTime;
    
    /**
     * 勤怠情報。
     */
    private String scheduleName;
    
    /**
     * 職員情報。
     */
    @ManyToOne
    private UserInfo userInfo;
 
}
