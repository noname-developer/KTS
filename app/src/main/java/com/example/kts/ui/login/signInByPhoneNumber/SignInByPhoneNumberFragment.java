package com.example.kts.ui.login.signInByPhoneNumber;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.R;
import com.example.kts.ui.login.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

public class SignInByPhoneNumberFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private EditText etNum;
    private TextInputLayout tnlNum;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sign_in_by_phone_number, container, false);
        etNum = root.findViewById(R.id.editText_num);
        tnlNum = root.findViewById(R.id.textinput_num);
        MaterialButton btnGetCode = root.findViewById(R.id.button_getCode);

        btnGetCode.setOnClickListener(view -> {
            String phoneNum = etNum.getText().toString();
            loginViewModel.onCodeClick(phoneNum);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etNum.getApplicationWindowToken(), 0);
        });
        etNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel.errorNum.observe(getViewLifecycleOwner(), s -> tnlNum.setError(s));
    }
}