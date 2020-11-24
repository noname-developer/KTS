package com.example.kts.ui.login.choiceOfRole;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.R;
import com.example.kts.ui.login.LoginViewModel;
import com.example.kts.ui.main.MainActivity;

public class ChoiceOfRoleFragment extends Fragment {

    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choice_of_role, container, false);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        Button btnTeacher = root.findViewById(R.id.button_teacher);
        Button btnStudent = root.findViewById(R.id.button_student);
        Button btnSkip = root.findViewById(R.id.button_skip_auth);

        btnTeacher.setOnClickListener(view -> loginViewModel.onTeacherClick());

        btnStudent.setOnClickListener(view -> loginViewModel.onStudentClick());

        btnSkip.setOnClickListener(view -> startActivity(new Intent(getActivity(), MainActivity.class)));

        return root;
    }

}