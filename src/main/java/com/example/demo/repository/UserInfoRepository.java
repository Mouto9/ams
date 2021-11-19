package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
	UserInfo findById(long id);
	List<UserInfo>findAllByOrderByUsername();
    UserInfo findByUsername(String username);
    boolean existsByUsername(String username);
}
