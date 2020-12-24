package com.example.kts.data.model.sqlite;

import androidx.room.TypeConverter;

import com.example.kts.utils.DateFormatUtil;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public String toString(Date date) {
        return DateFormatUtil.convertDateToString(date, DateFormatUtil.YYYY_MM_DD);
    }

    @TypeConverter
    public Date toDate(String date) {
        return DateFormatUtil.convertStringToDate(date, DateFormatUtil.YYYY_MM_DD);
    }

}