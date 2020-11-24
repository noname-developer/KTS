package com.example.kts.calendar;

import com.example.kts.calendar.model.Week;

import java.util.Date;

public interface WeekCalendarListener {

    void onDaySelect(Date date);

    void onWeekSelect(Week week);

    interface OnLoadListener {

        void onWeekLoad(Week week);
    }
}
