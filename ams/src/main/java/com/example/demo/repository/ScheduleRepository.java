package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Schedule;
import com.example.demo.model.UserInfo;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	List<Schedule> findByUserInfo(UserInfo userInfo);
	List<Schedule> findByScheduleDate(Date date);
	List<Schedule> findByScheduleDateAndScheduleName(Date date, String name);
	@Transactional
	void deleteByUserInfoId(long id);
	boolean existsByScheduleDate(Date date);
}
