package com.example.kts.calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kts.R;
import com.example.kts.calendar.model.Week;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekCalendarView extends LinearLayout {

    public static int CENTRAL_ITEM_POSITION = 3;
    private final WeekPageAdapter adapter = new WeekPageAdapter();
    private ViewPager2 viewPagerWeek;
    private final Context context;
    private WeekCalendarListener listener;

    private WeekCalendarListener.OnLoadListener loadListener;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    public WeekCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        this.context = context;
        setUp();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter.getData().isEmpty()) {
            addFirstWeeks();
            viewPagerWeek.setCurrentItem(CENTRAL_ITEM_POSITION, false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pageChangeCallback = null;
    }

    private void setUp() {
        initControl();
        viewPagerWeek.setAdapter(adapter);
        pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                listener.onWeekSelect(adapter.getData().get(position));
                adapter.notifyGriViewAdapter(position);
                if (position == adapter.getData().size() - 1) {
                    Calendar calendar = Calendar.getInstance();
                    Week week = adapter.getItem(position);
                    calendar.setTime(week.getDate(6));
                    calendar.add(Calendar.DATE, 1);
                    loadFewWeeks(calendar);
                    Toast.makeText(context, "load weeks", Toast.LENGTH_SHORT).show();
                }
            }
        };
        viewPagerWeek.registerOnPageChangeCallback(pageChangeCallback);
    }

    private void addFirstWeeks() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_MONTH, -CENTRAL_ITEM_POSITION);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<Week> weekList = new ArrayList<>();
        Week week;
        while (weekList.size() < 7) {
            week = getWeek(calendar);
            if (weekList.size() == CENTRAL_ITEM_POSITION) {
                week.findAndSetCurrentDay();
            }
            weekList.add(week);
        }
        adapter.setData(weekList);
    }

    private void loadFewWeeks(Calendar calendar) {
        List<Week> weekList = adapter.getData();
        for (int i = 0; i < 7; i++) {
            weekList.add(getWeek(calendar));
        }
    }

    @NotNull
    @Contract("_ -> new")
    private Week getWeek(Calendar calendar) {
        ArrayList<Date> daysOfWeekList = new ArrayList<>();
        int selectedDay = -1;

        while (daysOfWeekList.size() < 7) {
            daysOfWeekList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return new Week(daysOfWeekList, selectedDay);
    }

    private void initControl() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_week, this);
        viewPagerWeek = findViewById(R.id.viewpager_week);
    }

    public void setListener(WeekCalendarListener listener) {
        this.listener = listener;
        adapter.setListener(listener);
    }

    public void setLoadListener(WeekCalendarListener.OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.weekList = adapter.getData();

        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        adapter.setData(savedState.weekList);
    }

    public void setSelectDate(@NotNull Date selectDate) {
        Calendar calSelect = Calendar.getInstance();
        Calendar calFirstDateOfSelectedWeek = Calendar.getInstance();
        calSelect.setTime(selectDate);
        Week week = adapter.getItem(viewPagerWeek.getCurrentItem());
        calFirstDateOfSelectedWeek.setTime(week.getDate(0));

        int weekBySelectDate = calSelect.get(Calendar.WEEK_OF_YEAR);
        int weekByFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.WEEK_OF_YEAR);
        int yearBySelectDate = calSelect.get(Calendar.YEAR);
        int yearBySFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.YEAR);

        int offsetScroll = 0;
        while (yearBySelectDate != yearBySFirstDateOfSelectedWeek) {
            if (yearBySelectDate < yearBySFirstDateOfSelectedWeek) {
                offsetScroll--;
            } else {
                offsetScroll++;
            }
            week = adapter.getItem(getOffsetPosition(offsetScroll));
            calFirstDateOfSelectedWeek.setTime(week.getDate(0));
            weekByFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.WEEK_OF_YEAR);
            yearBySFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.YEAR);
        }

        if (weekByFirstDateOfSelectedWeek == 1) {
            offsetScroll--;
            week = adapter.getItem(getOffsetPosition(offsetScroll));
            calFirstDateOfSelectedWeek.setTime(week.getDate(0));
            weekByFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.WEEK_OF_YEAR);
            yearBySFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.YEAR);
        }

        while (weekBySelectDate != weekByFirstDateOfSelectedWeek) {
            if (weekBySelectDate < weekByFirstDateOfSelectedWeek) {
                Log.d("lol", "FUTURE WEK: " + offsetScroll);
                offsetScroll--;
            } else {
                offsetScroll++;
                Log.d("lol", "LAST WEEK: " + offsetScroll + " " + weekByFirstDateOfSelectedWeek + " " + yearBySFirstDateOfSelectedWeek);
            }
            week = adapter.getItem(getOffsetPosition(offsetScroll));
            calFirstDateOfSelectedWeek.setTime(week.getDate(0));
            weekByFirstDateOfSelectedWeek = calFirstDateOfSelectedWeek.get(Calendar.WEEK_OF_YEAR);
        }
        adapter.setCheckDay(getOffsetPosition(offsetScroll));
        viewPagerWeek.setCurrentItem(getOffsetPosition(offsetScroll));
    }

    private int getOffsetPosition(int offsetScroll) {
        return viewPagerWeek.getCurrentItem() + offsetScroll;
    }

    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @NotNull
            @Contract("_ -> new")
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @NotNull
            @Contract(value = "_ -> new", pure = true)
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        List<Week> weekList;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            //Nothing
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            //Nothing
        }
    }
}
