package com.example.kts.ui.adminPanel;

import androidx.lifecycle.ViewModel;

import com.example.SingleLiveData;
import com.example.kts.R;
import com.example.kts.data.model.domain.ListItem;

import java.util.Arrays;
import java.util.List;

public class AdminPanelViewModel extends ViewModel {

    SingleLiveData<?> openTimetableEditorFragment = new SingleLiveData<>();
    SingleLiveData<?> openChoiceOfGroupFragment = new SingleLiveData<>();
    SingleLiveData<?> openTeachersFragment = new SingleLiveData<>();

    public List<ListItem> getItemList() {
        return Arrays.asList(
                new ListItem("", "Расписание уроков"),
                new ListItem("", "Группы"),
                new ListItem("", "Преподаватели")
        );
    }

    public void onItemClick(int position) {
        switch (position) {
            case 0:
                openTimetableEditorFragment.call();
                break;
            case 1:
                openChoiceOfGroupFragment.call();
                break;
            case 2:
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
