package com.example.kts.utils;

import android.content.Context;

public final class Util {

    public static int pxToDp(Context context, float dpValue) {
        float dp = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * dp); // margin in pixels
    }
}
