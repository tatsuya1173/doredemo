package com.example.demo.repository;

import com.example.demo.model.Attendance;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 指定ユーザー＆日付で1件だけ取得（出勤・退勤・休憩ボタン用）
    Attendance findByUserAndDate(User user, LocalDate date);

    // 指定ユーザーの全勤怠レコードを取得（ダッシュボード全体表示用）
    List<Attendance> findAllByUser(User user);

    // 指定ユーザーの指定月の勤怠（今は未使用だけど月フィルタ時に使うと便利）
    List<Attendance> findAllByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
