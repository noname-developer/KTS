package com.example.kts.ui.adminPanel.timtableEditor;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class TimetableEditorFragment extends Fragment {

    public static final int PICKFILE_RESULT_CODE = 1;
    private TimetableEditorViewModel viewModel;
    private Button btnLoadTimetableDoc;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String s = data.getDataString();
            String path = uri.getPath().replaceAll("/document/raw:", "");
            File file = new File(path);
            viewModel.onSelectedFile(file);
            Toast.makeText(getActivity(), "path: " + path, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TimetableEditorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timetable_editor, container, false);
        btnLoadTimetableDoc = root.findViewById(R.id.button_load_timetable_doc);

        btnLoadTimetableDoc.setOnClickListener(view -> viewModel.onLoadTimetableDocClick());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.openFilePicker.observe(getViewLifecycleOwner(), (Observer<Object>) o -> {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICKFILE_RESULT_CODE);
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Выберите документ с расписанием за" + " N " + "курс");
            startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        });
        viewModel.result.observe(getViewLifecycleOwner(), s ->
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show());
    }
}