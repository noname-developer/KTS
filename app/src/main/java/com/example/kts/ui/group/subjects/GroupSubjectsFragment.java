package com.example.kts.ui.group.subjects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.ui.adapters.SubjectTeacherAdapter;
import com.example.kts.ui.confirm.ConfirmDialog;
import com.example.kts.ui.group.choiceOfSubjectTeacher.ChoiceOfSubjectTeacherDialog;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class GroupSubjectsFragment extends Fragment {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private GroupSubjectsViewModel viewModel;
    private SubjectTeacherAdapter adapter;
    private ActionMode actionMode;
    private RecyclerView recyclerView;

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
        recyclerView = view.findViewById(R.id.recyclerview_subject_teacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setListeners(position -> viewModel.onSubjectTeacherItemClick(position),
                position -> viewModel.onSubjectTeacherLongItemClick(position));
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
        GroupSubjectsViewModelFactory viewModelFactory = new GroupSubjectsViewModelFactory(getActivity().getApplication(),
                getArguments().getString(GROUP_UUID));
        viewModel = new ViewModelProvider(this, viewModelFactory).get(GroupSubjectsViewModel.class);
        viewModel.openChoiceSubjectTeacherDialog.observe(getViewLifecycleOwner(), subjectTeacher -> {
            ChoiceOfSubjectTeacherDialog dialog = ChoiceOfSubjectTeacherDialog.newInstance(
                    subjectTeacher.getGroupUuid(),
                    subjectTeacher.getSubject().getUuid(),
                    subjectTeacher.getTeacher().getUuid());
            dialog.show(getChildFragmentManager(), "CHOICE_OF_SUBJECT_TEACHER_DIALOG");
        });
        viewModel.subjectsTeachers.observe(getViewLifecycleOwner(), subjectsTeachers -> {
            adapter.submitList(subjectsTeachers);
            adapter.notifyDataSetChanged();
        });
        viewModel.openConfirmation.observe(getViewLifecycleOwner(), titleWithSubtitlePair -> {
            ConfirmDialog dialog = ConfirmDialog.newInstance(titleWithSubtitlePair.first, titleWithSubtitlePair.second);
            dialog.show(getChildFragmentManager(), null);
        });
        viewModel.selectItem.observe(getViewLifecycleOwner(), positionWithSelectPair ->
                selectSubjectTeacherItem(positionWithSelectPair.first, positionWithSelectPair.second));
        viewModel.actionMode.observe(getViewLifecycleOwner(), enable -> {
            if (enable) {
                startActionMode();
            } else {
                finishActionMode();
            }
        });
        viewModel.clearItemsSelection.observe(getViewLifecycleOwner(), positions ->
                positions.forEach(position -> selectSubjectTeacherItem(position, false)));
    }

    private void startActionMode() {
        actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.group_subjects_action, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                viewModel.onActionItemClick(item.getItemId());
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                viewModel.onDestroyActionMode();
            }
        });
    }

    public void selectSubjectTeacherItem(int position, boolean select) {
        SubjectTeacherAdapter.SubjectTeacherHolder holder = (SubjectTeacherAdapter.SubjectTeacherHolder) recyclerView.findViewHolderForLayoutPosition(position);
        holder.setSelect(select);
    }

    private void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }
}