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
@Getter
@Setter
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date scheduleDate;
    
    private String scheduleName;
    
    @ManyToOne
    private UserInfo userInfo;
 
}
