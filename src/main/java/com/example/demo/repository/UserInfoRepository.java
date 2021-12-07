package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserInfo;

/**
 * 職員情報リポジトリ。
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
	/**
	 * @param id 職員情報テーブルID。
	 * @return 職員情報。
	 */
	UserInfo findById(long id);
	/**
	 * @return 職員情報リスト。
	 */
	List<UserInfo>findAllByOrderByUsername();
    /**
     * @param username 職員情報ID。
     * @return 職員情報。
     */
    UserInfo findByUsername(String username);
    /**
     * @param username 職員情報ID。
     * @return 真偽値。
     */
    boolean existsByUsername(String username);
}
