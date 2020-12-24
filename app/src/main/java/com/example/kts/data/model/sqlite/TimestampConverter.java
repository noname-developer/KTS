package com.example.kts.data.model.sqlite;

import androidx.room.TypeConverter;

import com.example.kts.utils.DateFormatUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class TimestampConverter {

    @TypeConverter
    public long toLong(@NotNull Date date) {
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(long timestamp) {
        return new Date(timestamp);
    }

}