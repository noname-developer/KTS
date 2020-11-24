package com.example.kts.ui.adminPanel.groupEditor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.RxBusChoiceOfGroup;
import com.example.kts.RxBusSubjectTeacher;
import com.example.kts.ui.adapters.SubjectTeacherAdapter;
import com.example.kts.ui.adminPanel.groupEditor.choiceSubjectTeacher.ChoiceOfSubjectTeacherDialog;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class GroupEditorFragment extends Fragment {

    private GroupEditorViewModel viewModel;
    private SubjectTeacherAdapter adapter;
    private RecyclerView recyclerView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_editor, container, false);
        adapter = new SubjectTeacherAdapter();
        recyclerView = view.findViewById(R.id.recyclerview_subject_teacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> viewModel.onSubjectTeacherItemClick(position));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GroupEditorViewModel.class);
        viewModel.openChoiceSubjectTeacherDialog.observe(getViewLifecycleOwner(), subjectTeacher -> {
            RxBusSubjectTeacher.getInstance().postSelectSubjectTeacherEvent(subjectTeacher);
            ChoiceOfSubjectTeacherDialog dialog = new ChoiceOfSubjectTeacherDialog();
            dialog.show(getChildFragmentManager(), "CHOICE_OF_SUBJECT_TEACHER_DIALOG");
        });
        compositeDisposable.add(RxBusChoiceOfGroup.getInstance().getSelectGroupEvent().subscribe(o -> {
            Log.d("lol", "onViewCreated LOL: `");
            String groupUuid = String.valueOf(o);
            viewModel.onGroupUuidReceived(groupUuid);
            viewModel.getGroupInfo.observe(getViewLifecycleOwner(), groupInfo -> {
                adapter.setData(groupInfo);
                adapter.notifyDataSetChanged();
            });
        }));
    }
}