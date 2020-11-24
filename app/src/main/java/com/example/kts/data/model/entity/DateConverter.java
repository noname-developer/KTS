package com.example.kts.data.model.entity;

import androidx.room.TypeConverter;

import com.example.kts.utils.DateFormatUtils;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public String toString(Date date) {
        return DateFormatUtils.convertDateToString(date, DateFormatUtils.YYYY_MM_DD);
    }

    @TypeConverter
    public Date toDate(String date) {
        return DateFormatUtils.convertStringToDate(date, DateFormatUtils.YYYY_MM_DD);
    }

}