package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Attendance;
import com.example.demo.model.UserInfo;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	List<Attendance> findByUserInfo(UserInfo userInfo);
	@Transactional
	void deleteByUserInfoId(long id);
}
