package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Attendance;
import com.example.demo.model.UserInfo;

/**
 * 出勤情報リポジトリ。
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	/**
	 * @param id 出勤情報テーブルID。
	 * @return 出勤情報。
	 */
	Attendance findById(long id);

	/**
	 * @param user 職員情報。
	 * @return 出勤情報リスト。
	 */
	List<Attendance> findByUserInfo(UserInfo user);

	/**
	 * @param start 開始日。
	 * @param end 終了日。
	 * @param user 職員情報。
	 * @return 真偽値。
	 */
	Boolean existsByAttendanceTimeBetweenAndUserInfo(Date start,Date end,UserInfo user);

	/**
	 * @param id 出勤情報テーブルID。
	 */
	@Transactional
	void deleteByUserInfoId(long id);
}
