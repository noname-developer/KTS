package com.example.kts.ui.timetable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.widgets.calendar.WeekCalendarListener;
import com.example.widgets.calendar.WeekCalendarView;
import com.example.widgets.calendar.model.Week;
import com.example.kts.ui.DividerItemDecorator;
import com.example.kts.ui.adapters.LessonForGroupAdapter;
import com.example.kts.ui.main.MainViewModel;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Date;

import static com.example.kts.utils.Util.pxToDp;

public class TimetableFragment extends Fragment implements WeekCalendarListener, WeekCalendarListener.OnLoadListener {

    private TimetableViewModel viewModel;
    private LessonForGroupAdapter adapter;
    private WeekCalendarView timetableView;
    private AppBarLayout appBarLayout;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TimetableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);

        appBarLayout = root.findViewById(R.id.app_bar);
        timetableView = root.findViewById(R.id.calendar_timetable);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_lessons);

        adapter = new LessonForGroupAdapter(viewModel.getLessonTime());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        timetableView.setListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.onScroll(recyclerView.canScrollVertically(-1), recyclerView.getHeight() - pxToDp(getActivity(), 26));
            }
        });
        adapter.setOnLessonItemClickListener(new LessonForGroupAdapter.OnLessonItemClickListener() {
            @Override
            public void onHomeworkChecked(boolean checked) {
                viewModel.onHomeworkChecked(checked);
            }

            @Override
            public void onItemClick(int position) {

            }
        });

        viewModel.calendarShadowVisibility.observe(getViewLifecycleOwner(), visibility -> appBarLayout.setSelected(visibility));

        viewModel.allLessonsForDay.observe(getViewLifecycleOwner(), lessonsOfGroup -> {
            adapter.submitList(lessonsOfGroup);

            //todo для теста
//            adapter.submitList();
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.selectedDate.observe(getViewLifecycleOwner(), timetableView::setSelectDate);
        viewModel.toolbarName.observe(getViewLifecycleOwner(), title ->
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title));
    }

    @Override
    public void onDaySelect(Date date) {
        viewModel.onDaySelect(date);
    }

    @Override
    public void onWeekSelect(Week week) {
        viewModel.onWeekSelect(week);
        Log.d("lol", "onWeekSelect: ");
    }

    @Override
    public void onWeekLoad(Week week) {
        viewModel.onWeekLoad(week);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}