package com.example.widgets.calendar;

import com.example.widgets.calendar.model.Week;

import java.util.Date;

public interface WeekCalendarListener {

    void onDaySelect(Date date);

    void onWeekSelect(Week week);

    interface OnLoadListener {

        void onWeekLoad(Week week);
    }
}
