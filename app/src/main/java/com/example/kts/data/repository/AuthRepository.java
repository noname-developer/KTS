package com.example.kts.data.repository;

import android.app.Application;
import android.util.Log;

import com.example.kts.data.FireEvent;
import com.example.kts.data.model.sqlite.UserEntity;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private ObservableEmitter<FireEvent<String>> emitter;
    private String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    public AuthRepository(Application application) {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }


    public Observable<FireEvent<String>> sendUserPhoneNumber(@NotNull UserEntity userEntity) {
        return Observable.create(new ObservableOnSubscribe<FireEvent<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<FireEvent<String>> emitter) {
                AuthRepository.this.emitter = emitter;
                callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d("lol", "onVerificationFailed: ", e);
                        emitter.onError(e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        AuthRepository.this.verificationId = verificationId;
                    }
                };
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+16505553434",
                        60,
                        TimeUnit.SECONDS,
                        TaskExecutors.MAIN_THREAD,
                        callbacks);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(TaskExecutors.MAIN_THREAD, task -> {
            if (task.isSuccessful()) {
                emitter.onNext(new FireEvent<String>().complete(credential.getSmsCode()));
                emitter.onComplete();
                callbacks = null;
            } else {
                emitter.onNext(new FireEvent<String>().error(task.getException()));
            }
        });
    }

    public void tryAuthWithPhoneNumByCode(String code) {
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }

    private void saveGroupDate() {
        //todo
//        groupPreference.setGroupCourse();
    }
}
