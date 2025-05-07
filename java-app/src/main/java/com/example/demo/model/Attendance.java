package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 勤怠は「1人のユーザーに対して複数の出勤記録」が紐づく関係（多対一）
    @ManyToOne
    @JoinColumn(name = "user_id") // 外部キー名：user_id
    private User user;

    // 勤怠記録の日付（例：2025-05-07）
    private LocalDate date;

    // 出勤・退勤・休憩の各時刻
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;

    // --- Getter & Setter ---
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public LocalTime getBreakStartTime() {
        return breakStartTime;
    }

    public void setBreakStartTime(LocalTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    public LocalTime getBreakEndTime() {
        return breakEndTime;
    }

    public void setBreakEndTime(LocalTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }

    /**
     * 実働時間（出勤〜退勤の間から休憩時間を引いたもの）を返す
     * 両方揃ってなければ Duration.ZERO を返す
     */
    public Duration getWorkingDuration() {
        if (checkinTime == null || checkoutTime == null) {
            return Duration.ZERO;
        }

        Duration total = Duration.between(checkinTime, checkoutTime);

        if (breakStartTime != null && breakEndTime != null) {
            Duration rest = Duration.between(breakStartTime, breakEndTime);
            return total.minus(rest);
        }

        return total;
    }
}
