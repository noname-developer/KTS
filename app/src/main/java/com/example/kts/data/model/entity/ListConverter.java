package com.example.kts.data.model.entity;

import androidx.room.TypeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListConverter {

    @TypeConverter
    public String fromImageUrlList(@NotNull List<String> imagesUrlList) {
        return imagesUrlList.stream().collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> toImageUrlList(@NotNull String data) {
        return Arrays.asList(data.split(","));
    }
}
