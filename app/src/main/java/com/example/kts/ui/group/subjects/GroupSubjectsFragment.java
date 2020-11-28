package com.example.kts.ui.group.subjects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.RxBusSubjectTeacher;
import com.example.kts.ui.adapters.SubjectTeacherAdapter;
import com.example.kts.ui.group.choiceSubjectTeacher.ChoiceOfSubjectTeacherDialog;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class GroupSubjectsFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private GroupSubjectsViewModel viewModel;
    private SubjectTeacherAdapter adapter;

    @NotNull
    public static GroupSubjectsFragment newInstance(String groupUuid) {
        GroupSubjectsFragment fragment = new GroupSubjectsFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_UUID, groupUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_subjects, container, false);
        adapter = new SubjectTeacherAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_subject_teacher);
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
        viewModel = new ViewModelProvider(this).get(GroupSubjectsViewModel.class);
        viewModel.onGroupUuidReceived(getArguments().getString(GROUP_UUID));
        viewModel.openChoiceSubjectTeacherDialog.observe(getViewLifecycleOwner(), subjectTeacher -> {
            RxBusSubjectTeacher.getInstance().postSelectSubjectTeacherEvent(subjectTeacher);
            ChoiceOfSubjectTeacherDialog dialog = new ChoiceOfSubjectTeacherDialog();
            dialog.show(getChildFragmentManager(), "CHOICE_OF_SUBJECT_TEACHER_DIALOG");
        });
        viewModel.getGroupInfo.observe(getViewLifecycleOwner(), groupInfo -> {
            adapter.setData(groupInfo);
            adapter.notifyDataSetChanged();
        });
    }
}