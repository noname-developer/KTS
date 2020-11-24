package com.example.kts.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.data.repository.AuthRepository;

public class SplashViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    public MutableLiveData<NextActivity> nextScreenMLiveData = new MutableLiveData<>();

    public SplashViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);

        if (authRepository.getCurrentUser() != null) {
            nextScreenMLiveData.setValue(NextActivity.MAIN);
        } else {
            nextScreenMLiveData.setValue(NextActivity.LOGIN);
        }
    }

    enum NextActivity {
        MAIN, LOGIN
    }
}
