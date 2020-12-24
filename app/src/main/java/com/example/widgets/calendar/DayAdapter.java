package com.example.widgets.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kts.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayAdapter extends ArrayAdapter<Date> {

    private LayoutInflater inflater;
    private List<Date> dayOfWeekList;
    private Calendar calendar = Calendar.getInstance();

    public DayAdapter(@NonNull Context context, int resource, List<Date> dayOfWeekList) {
        super(context, resource, dayOfWeekList);
        this.dayOfWeekList = dayOfWeekList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_date, parent, false);
        }

        calendar.setTime(dayOfWeekList.get(position));
        ((TextView) view).setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        return view;
    }
}
