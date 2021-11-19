package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
	UserInfo findById(long id);
    UserInfo findByUsername(String username);
    boolean existsByUsername(String username);
}
