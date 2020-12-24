package com.example.kts.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.kts.SingleLiveData;
import com.example.kts.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Date;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> appbarExpand = new MutableLiveData<>(true);
    public MutableLiveData<Boolean> appbarShadowVisibility = new MutableLiveData<>(false);
    public MutableLiveData<Integer> appBarFlags = new MutableLiveData<>();
    public MutableLiveData<Integer> currentMenuOptions = new MutableLiveData<>(R.menu.home_options);
    public SingleLiveData<Date> selectedDate = new SingleLiveData<>();
    private final MainInteractor interactor;

    public MainViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        interactor = new MainInteractor(application);
    }

    public void onScroll(boolean scrollVertically, int range, int height) {
        if (scrollVertically) {
            if (!appbarShadowVisibility.getValue())
                appbarShadowVisibility.postValue(true);
        } else {
            if (range > height) {
                if (!appbarExpand.getValue()) {
                    if (!appbarExpand.getValue()) {
                        appBarFlags.setValue(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                    }
                }
            } else {
                appBarFlags.setValue(0);
            }
            appbarShadowVisibility.postValue(false);
        }
    }

    public void onDestinationChanged(int itemId) {
        switch (itemId) {
            case R.id.menu_timetable:
                currentMenuOptions.setValue(R.menu.timtable_options);
                break;
            case R.id.menu_home:
                currentMenuOptions.setValue(R.menu.home_options);
                break;
            case R.id.menu_group:
            case R.id.group_editor:
                currentMenuOptions.setValue(R.menu.group_options);
                break;
        }
    }

    public void onOptionItemSelect(int itemId) {
        switch (itemId) {
            case R.id.option_select_today:
                selectedDate.setValue(new Date());
                break;

        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        interactor.removeRegistrations();
    }

    public String getGroupUuid() {
        return "ololololol";
    }
}
