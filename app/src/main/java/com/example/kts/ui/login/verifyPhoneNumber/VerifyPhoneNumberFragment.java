package com.example.kts.ui.login.verifyPhoneNumber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.R;
import com.example.kts.ui.login.LoginViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VerifyPhoneNumberFragment extends Fragment {

    private VerifyPhoneNumberViewModel viewModel;
    private TextInputLayout tnlCode1, tnlCode2, tnlCode3, tnlCode4, tnlCode5, tnlCode6;
    private EditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;
    private Button btnAuth;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LoginViewModel loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(VerifyPhoneNumberViewModel.class);
        View root = inflater.inflate(R.layout.fragment_verify_phone_number, container, false);

        tnlCode1 = root.findViewById(R.id.textInputLayout_code_1);
        tnlCode2 = root.findViewById(R.id.textInputLayout_code_2);
        tnlCode3 = root.findViewById(R.id.textInputLayout_code_3);
        tnlCode4 = root.findViewById(R.id.textInputLayout_code_4);
        tnlCode5 = root.findViewById(R.id.textInputLayout_code_5);
        tnlCode6 = root.findViewById(R.id.textInputLayout_code_6);

        etCode1 = root.findViewById(R.id.editText_code_1);
        etCode2 = root.findViewById(R.id.editText_code_2);
        etCode3 = root.findViewById(R.id.editText_code_3);
        etCode4 = root.findViewById(R.id.editText_code_4);
        etCode5 = root.findViewById(R.id.editText_code_5);
        etCode6 = root.findViewById(R.id.editText_code_6);
        btnAuth = root.findViewById(R.id.button_auth);

        RxTextView.textChanges(etCode1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    viewModel.onCharTyped(getTypedCode());
                    if (!s.toString().isEmpty()) etCode2.requestFocus();
                });


        RxTextView.textChanges(etCode2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    viewModel.onCharTyped(getTypedCode());
                    if (!s.toString().isEmpty()) etCode3.requestFocus();
                });
        RxTextView.textChanges(etCode3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .subscribe(s -> {
                    viewModel.onCharTyped(s);
                    if (!s.isEmpty()) etCode4.requestFocus();
                });
        RxTextView.textChanges(etCode4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    viewModel.onCharTyped(getTypedCode());
                    if (!s.toString().isEmpty()) etCode5.requestFocus();
                });
        RxTextView.textChanges(etCode5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    viewModel.onCharTyped(getTypedCode());
                    if (!s.toString().isEmpty()) etCode6.requestFocus();
                });
        RxTextView.textChanges(etCode6)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    viewModel.onCharTyped(getTypedCode());
                    if (!s.toString().isEmpty()) {etCode6.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etCode6.getApplicationWindowToken(), 0);}
                });

        btnAuth.setOnClickListener(view -> viewModel.tryAuthWithPhoneNumByCode(getTypedCode()));

        loginViewModel.verifyUser.observe(getViewLifecycleOwner(), user -> viewModel.onCodeClick(user));

        viewModel.authSuccessful.observe(getViewLifecycleOwner(), code -> {
            fillFieldsWithCode(code);
            loginViewModel.onSuccessfulLogin();
        });

        viewModel.errorToManyRequest.observe(getViewLifecycleOwner(), (Observer<Object>) o -> showErrorManyRequest());

        viewModel.errorInvalidRequest.observe(getViewLifecycleOwner(), (Observer<Object>) o -> showErrorInvalidCode());

        viewModel.btnAuthVisibility.observe(getViewLifecycleOwner(), visible -> {
            Log.d("lol", "onCreateView: " + visible);
            if (visible) {
                btnAuth.setVisibility(View.VISIBLE);
                btnAuth.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnAuth.setScaleX(1);
                                btnAuth.setScaleY(1);
                            }
                        });
                Log.d("lol", "anim: ");
            } else {
                btnAuth.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnAuth.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
        return root;
    }

    private void showErrorManyRequest() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Ошибка")
                .setMessage("Превышен лимит попыток входа. Повторите позже")
                .setPositiveButton("Ок", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void showErrorInvalidCode() {
        List<TextInputLayout> textInputLayoutList = Arrays.asList(tnlCode1, tnlCode2, tnlCode3, tnlCode4, tnlCode5, tnlCode6);
        for (int i = 0; i < 6; i++) {
            textInputLayoutList.get(i).setError(" ");
        }
        Toast.makeText(getActivity(), "Неправильный код!", Toast.LENGTH_SHORT).show();
    }

    private void fillFieldsWithCode(@NotNull String code) {
        List<EditText> editTextList = Arrays.asList(etCode1, etCode2, etCode3, etCode4, etCode5, etCode6);
        for (int i = 0; i < code.length(); i++) {
            editTextList.get(i).setText(String.valueOf(code.charAt(i)));
        }
        Toast.makeText(getActivity(), "Готово!", Toast.LENGTH_SHORT).show();
    }

    @NotNull
    private String getTypedCode() {
        String s = etCode1.getText().toString() +
                etCode2.getText().toString() +
                etCode3.getText().toString() +
                etCode4.getText().toString() +
                etCode5.getText().toString() +
                etCode6.getText().toString();
        Log.d("lol", "getTypedCode: " + s);
        return s;
    }
}