package com.example.kts.ui.login.verifyPhoneNumber;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.kts.SingleLiveData;
import com.example.kts.data.model.sqlite.UserEntity;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VerifyPhoneNumberViewModel extends AndroidViewModel {

    private final VerifyPhoneNumberInteractor interactor;
    public SingleLiveData<Boolean> btnAuthVisibility = new SingleLiveData<>(false);
    public MutableLiveData<Boolean> loadingDialogVisibility = new MutableLiveData<>();
    public SingleLiveData<String> authSuccessful = new SingleLiveData<>();
    public SingleLiveData<?> errorToManyRequest = new SingleLiveData<>();
    public SingleLiveData<?> errorInvalidRequest = new SingleLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public VerifyPhoneNumberViewModel(Application application) {
        super(application);
        interactor = new VerifyPhoneNumberInteractor(application);
    }

    void onCodeClick(@NotNull UserEntity userEntity) {
        Disposable subscribe = interactor.sendUserPhoneNumber(userEntity).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getStatus()) {
                        case SUCCESSFUL:
                            Log.d("lol", "SUCCESSFUL VIEWMODEL: ");
                            authSuccessful.setValue(event.getData());
                        case LOADING:
                            Log.d("lol", "LOADING VIEWMODEL: ");
                        case ERROR: {
                            if (event.getException() instanceof FirebaseTooManyRequestsException) {
                                errorToManyRequest.call();
                            } else if (event.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorInvalidRequest.call();
                            }
                        }

                    }
                });
        compositeDisposable.add(subscribe);
    }

    public void tryAuthWithPhoneNumByCode(String code) {
        interactor.tryAuthWithPhoneNumByCode(code);
    }

    public void onCharTyped(@NotNull String code) {
        Log.d("lol", "onCharTyped: " + code);
        if (code.length() == 6) {
            Log.d("lol", "onCharTyped: TRUE");
            btnAuthVisibility.setValue(true);
        } else if (btnAuthVisibility.getValue()) {
            Log.d("lol", "onCharTyped: FALSE");
            btnAuthVisibility.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        Log.d("TAG", "onCleared: ");
        super.onCleared();
    }
}
