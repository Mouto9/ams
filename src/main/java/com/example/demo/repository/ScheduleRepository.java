package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Schedule;
import com.example.demo.model.UserInfo;

/**
 * 勤怠情報リポジトリ。
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	/**
	 * @param id 勤怠情報テーブルID。
	 * @return 勤怠情報。
	 */
	Schedule findById(long id);
	/**
	 * @param date 日付。
	 * @param user 職員情報。
	 * @return 勤怠情報。
	 */
	Schedule findByScheduleDateAndUserInfo(Date date,UserInfo user);

	/**
	 * @param user 職員情報。
	 * @return 勤怠情報リスト。
	 */
	List<Schedule> findByUserInfo(UserInfo user);
	/**
	 * @param user 職員情報。
	 * @return 勤怠情報リスト。
	 */
	List<Schedule> findByUserInfoAndScheduleTimeIsNotNull(UserInfo user);
	/**
	 * @param start 開始日。
	 * @param end 終了日。
	 * @return 勤怠情報リスト。
	 */
	List<Schedule> findByScheduleDateBetweenOrderByUserInfoUsername(Date start,Date end);

	/**
	 * @param id 勤怠情報テーブルID。
	 */
	@Transactional
	void deleteByUserInfoId(long id);
	/**
	 * @param user 職員情報。
	 * @param date 日付。
	 */
	@Transactional
	void deleteByUserInfoAndScheduleDate(UserInfo user, Date date);
}
