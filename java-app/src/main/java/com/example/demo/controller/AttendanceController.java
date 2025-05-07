package com.example.demo.controller;

import com.example.demo.model.Attendance;
import com.example.demo.model.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    // ※今はログイン未対応なので、user_id=1の仮ユーザーで固定処理
    private final Long currentUserId = 1L;

    // ========== 共通処理群（再利用するやつ） ==========

    // ユーザーを取得（いなかったらエラー）
    private User getCurrentUser() {
        return userRepository.findById(currentUserId).orElseThrow();
    }

    // 今日の日付（Asia/Tokyo 時間で）
    private LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Tokyo"));
    }

    // 現在時刻（Asia/Tokyo 時間で）
    private LocalTime nowTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalTime();
    }

    // 今日の勤怠を取得（なければ新しく作成）
    private Attendance findOrCreateTodayAttendance(User user) {
        return Optional.ofNullable(attendanceRepository.findByUserAndDate(user, today()))
                .orElseGet(() -> {
                    Attendance att = new Attendance();
                    att.setUser(user);
                    att.setDate(today());
                    return att;
                });
    }

    // 勤怠がある時だけ任意の更新処理を実行（ラムダで内容を渡す）
    private void updateTime(User user, Consumer<Attendance> updater) {
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today());
        if (attendance != null) {
            updater.accept(attendance); // ここで休憩開始とか退勤とかの更新を実行
            attendanceRepository.save(attendance);
        }
    }

    // ========== 打刻API（出勤・退勤・休憩） ==========

    // 出勤ボタンを押したとき
    @PostMapping("/attendance/checkin")
    public String checkin() {
        Attendance att = findOrCreateTodayAttendance(getCurrentUser());
        att.setCheckinTime(nowTime());
        attendanceRepository.save(att);
        return "redirect:/java/dashboard";
    }

    // 退勤ボタンを押したとき
    @PostMapping("/attendance/checkout")
    public String checkout() {
        updateTime(getCurrentUser(), att -> att.setCheckoutTime(nowTime()));
        return "redirect:/java/dashboard";
    }

    // 休憩開始ボタンを押したとき
    @PostMapping("/attendance/break-start")
    public String breakStart() {
        updateTime(getCurrentUser(), att -> att.setBreakStartTime(nowTime()));
        return "redirect:/java/dashboard";
    }

    // 休憩終了ボタンを押したとき
    @PostMapping("/attendance/break-end")
    public String breakEnd() {
        updateTime(getCurrentUser(), att -> att.setBreakEndTime(nowTime()));
        return "redirect:/java/dashboard";
    }

    // ========== ダッシュボード画面 ==========

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String month, Model model) {
        User user = getCurrentUser();

        // 月指定がなければ今月を対象にする
        YearMonth targetMonth;
        try {
            targetMonth = (month != null) ? YearMonth.parse(month) : YearMonth.now();
        } catch (Exception e) {
            targetMonth = YearMonth.now(); // パース失敗時のフォールバック
        }

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        // 今は月指定の絞り込みは使ってない（将来的に使うならstart〜endでfilter）
        List<Attendance> attendances = attendanceRepository.findAllByUserAndDateBetween(user, start, end);

        long workingDays = 0;                  // 出勤日数
        Duration totalDuration = Duration.ZERO; // 総労働時間
        List<Map<String, Object>> formatted = new ArrayList<>(); // 表示用に整形

        for (Attendance att : attendances) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", att.getDate());
            map.put("checkinTime", att.getCheckinTime());
            map.put("breakStartTime", att.getBreakStartTime());
            map.put("breakEndTime", att.getBreakEndTime());
            map.put("checkoutTime", att.getCheckoutTime());

            Duration dur = att.getWorkingDuration();
            map.put("workingDuration", String.format("%d時間%d分", dur.toHours(), dur.toMinutesPart()));

            // 出退勤が両方あるものだけを出勤日としてカウント
            if (att.getCheckinTime() != null && att.getCheckoutTime() != null) {
                workingDays++;
                totalDuration = totalDuration.plus(dur);
            }

            formatted.add(map);
        }

        // Thymeleafに渡すデータ
        model.addAttribute("attendances", formatted);
        model.addAttribute("month", targetMonth.toString());
        model.addAttribute("workingDays", workingDays);
        model.addAttribute("totalWorkingTime",
                String.format("%d時間%d分", totalDuration.toHours(), totalDuration.toMinutesPart()));

        return "dashboard";
    }
}
