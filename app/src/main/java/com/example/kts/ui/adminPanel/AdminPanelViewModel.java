package com.example.kts.ui.adminPanel;

import androidx.lifecycle.ViewModel;

import com.example.kts.SingleLiveData;
import com.example.kts.data.model.domain.ListItem;

import java.util.Arrays;
import java.util.List;

import static com.example.kts.ui.adapters.BaseViewHolder.TYPE_HEADER;

public class AdminPanelViewModel extends ViewModel {

    SingleLiveData<?> openTimetableEditor = new SingleLiveData<>();
    SingleLiveData<?> openChoiceOfGroupFragment = new SingleLiveData<>();
    SingleLiveData<?> openUserFinder = new SingleLiveData<>();

    public List<ListItem> getItemList() {
        List<ListItem> listItems = Arrays.asList(
                new ListItem("", "ic_search", "Найти что угодно"),
                new ListItem("", "ic_plus", "Добавить"),
                new ListItem("", "ic_book", "Уроки")
        );
        return listItems;
    }

    public void onItemClick(int position) {
        switch (position) {
            case 0:
                openUserFinder.call();
                break;
            case 1:
                openChoiceOfGroupFragment.call();
                break;
            case 2:
                openTimetableEditor.call();
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
