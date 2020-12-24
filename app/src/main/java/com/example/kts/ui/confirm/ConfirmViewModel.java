package com.example.kts.ui.confirm;

import androidx.lifecycle.ViewModel;

import com.example.kts.RxBusConfirm;

public class ConfirmViewModel extends ViewModel {

    public void onNegativeClick() {
        RxBusConfirm.getInstance().postEvent(false);
    }

    public void onPositiveClick() {
        RxBusConfirm.getInstance().postEvent(true);
    }
}
