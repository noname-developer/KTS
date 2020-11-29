package com.example.kts.utils;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateFormatUtil {

    private static final Calendar calToday = Calendar.getInstance();
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String LLLL = "LLLL";
    public static String LLLL_YYY = "LLLL yyyy";
    public static String DD_MM_YY = "dd.MM.yy";

    public static Date convertStringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date convertedDate = null;
        try {
            convertedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    @NotNull
    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Log.d("lol", "convertDateToString: " + date);
        return sdf.format(date);
    }

    @NotNull
    public static Date toUtc(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, -3);
        return c.getTime();
    }

    @NotNull
    public static String convertDateToStringHidingCurrentYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)) {
            return StringUtils.capitalize(convertDateToString(date, LLLL));
        } else {
            return StringUtils.capitalize(convertDateToString(date, LLLL_YYY));
        }
    }

    @NotNull
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static boolean validateDateOfString(String date , String format) {
        return convertStringToDate(date, format) != null;
    }
}
