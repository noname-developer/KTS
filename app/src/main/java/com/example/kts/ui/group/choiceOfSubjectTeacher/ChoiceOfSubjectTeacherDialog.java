package com.example.kts.ui.group.choiceOfSubjectTeacher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.kts.R;
import com.example.kts.customPopup.ListPopupWindowAdapter;
import com.example.kts.data.model.domain.ListItem;
import com.example.kts.ui.adapters.OnItemClickListener;
import com.example.kts.ui.confirm.ConfirmDialog;
import com.example.kts.customPopup.PopupBuilder;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class ChoiceOfSubjectTeacherDialog extends DialogFragment {

    private static final String SUBJECT_UUID = "SUBJECT_UUID";
    private static final String TEACHER_UUID = "TEACHER_UUID";
    private ImageView ivTeacherAvatar, ivSubjectIcon;
    private EditText etTeacherName, etSubjectName;
    private TextView tvTeacherName, tvSubjectName;
    private TextInputLayout tlTeacherName, tlSubjectName;
    private ChoiceOfSubjectTeacherViewModel viewModel;
    private AlertDialog alertDialog;
    private InputMethodManager imm;
    private ListPopupWindow popupWindow;

    @NotNull
    public static ChoiceOfSubjectTeacherDialog newInstance(String groupUuid, String subjectUuid, String teacherUuid) {
        ChoiceOfSubjectTeacherDialog fragment = new ChoiceOfSubjectTeacherDialog();
        Bundle args = new Bundle();
        args.putString(GROUP_UUID, groupUuid);
        args.putString(SUBJECT_UUID, subjectUuid);
        args.putString(TEACHER_UUID, teacherUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choice_subject_teacher, null);
        ivSubjectIcon = view.findViewById(R.id.imageView_subject_icon);
        ivTeacherAvatar = view.findViewById(R.id.imageView_teacher_avatar);
        etSubjectName = view.findViewById(R.id.editText_subject_name);
        etTeacherName = view.findViewById(R.id.editText_teacher_name);
        tlTeacherName = view.findViewById(R.id.textInputLayout_teacher_name);
        tlSubjectName = view.findViewById(R.id.textInputLayout_subject_name);
        tvTeacherName = view.findViewById(R.id.textView_teacher_name);
        tvSubjectName = view.findViewById(R.id.textView_subject_name);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        tvSubjectName.setOnClickListener(view13 -> viewModel.onSubjectNameClick());

        tvTeacherName.setOnClickListener(view12 -> viewModel.onTeacherNameClick());

        tlSubjectName.setEndIconOnClickListener(v -> viewModel.onEndIconSubjectNameClick());

        tlTeacherName.setEndIconOnClickListener(v -> viewModel.onEndIconTeacherNameClick());

        etSubjectName.setOnFocusChangeListener((view14, focus) -> viewModel.onSubjectNameFocusChange(focus));

        etTeacherName.setOnFocusChangeListener((view1, focus) -> viewModel.onTeacherNameFocusChange(focus));

        etSubjectName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onSubjectNameType(etSubjectName.getText().toString());
                closeKeyboard(etSubjectName);
                return true;
            }
            return false;
        });

        etTeacherName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onTeacherNameType(etTeacherName.getText().toString());
                closeKeyboard(etTeacherName);
                return true;
            }
            return false;
        });

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Выберите предмет и преподавателя")
                .setView(view)
                .setPositiveButton("Ок", (dialogInterface, i) -> viewModel.onPositiveClick())
                .setNegativeButton("Отмена", null)
                .setNeutralButton("Удалить", null);
        alertDialog = dialog.create();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.setOnShowListener(dialog13 -> {
            Button btnDelete = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            btnDelete.setTextColor(Color.RED);
            btnDelete.setOnClickListener(v -> viewModel.onDeleteClick());
        });
        return alertDialog;
    }

    private void closeKeyboard(@NotNull View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ChoiceOfSubjectTeacherViewModelFactory factory = new ChoiceOfSubjectTeacherViewModelFactory(
                getActivity().getApplication(),
                getArguments().getString(GROUP_UUID),
                getArguments().getString(SUBJECT_UUID),
                getArguments().getString(TEACHER_UUID)
        );
        viewModel = new ViewModelProvider(this, factory).get(ChoiceOfSubjectTeacherViewModel.class);
        viewModel.enablePositiveBtn.observe(getViewLifecycleOwner(), enabled ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled));

        viewModel.editTextSubjectNameVisibility.observe(getViewLifecycleOwner(), visible -> {
            tlSubjectName.setVisibility(visible ? View.VISIBLE : View.GONE);
            tvSubjectName.setVisibility(visible ? View.GONE : View.VISIBLE);
            if (visible) {
                etSubjectName.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                closeKeyboard(etSubjectName);
            }
        });

        viewModel.title.observe(getViewLifecycleOwner(), title -> alertDialog.setTitle(title));

        viewModel.editTextTeacherNameVisibility.observe(getViewLifecycleOwner(), visible -> {
            tlTeacherName.setVisibility(visible ? View.VISIBLE : View.GONE);
            tvTeacherName.setVisibility(visible ? View.GONE : View.VISIBLE);
            if (visible) {
                etTeacherName.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                closeKeyboard(etTeacherName);
            }
        });

        viewModel.selectSubject.observe(getViewLifecycleOwner(), subject -> {
            GlideToVectorYou.init()
                    .with(getActivity())
                    .getRequestBuilder()
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .load(subject.getIconUrl()).into(ivSubjectIcon);
            tvSubjectName.setText(subject.getName());
            etSubjectName.setText("");
        });

        viewModel.selectTeacher.observe(getViewLifecycleOwner(), teacher -> {
            Glide.with(getActivity())
                    .load(teacher.getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .into(ivTeacherAvatar);

            tvTeacherName.setText(teacher.getFullName());
            etTeacherName.setText("");
        });

        viewModel.foundedUsersPopup.observe(getViewLifecycleOwner(), items -> {
            popupWindow = new PopupBuilder().setPopupMenu(popupWindow, etTeacherName, items, R.layout.item_popup_avatar_content, position -> viewModel.onTeacherSelect(position));
            popupWindow.show();
        });

        viewModel.foundedSubjectsPopup.observe(getViewLifecycleOwner(), items -> {
            popupWindow = new PopupBuilder().setPopupMenu(popupWindow, etSubjectName, items, R.layout.item_popup_icon_content, position -> viewModel.onSubjectSelect(position));
            popupWindow.show();
        });

        viewModel.openConfirmation.observe(getViewLifecycleOwner(), titleWithSubtitlePair -> {
            ConfirmDialog dialog = ConfirmDialog.newInstance(titleWithSubtitlePair.first, titleWithSubtitlePair.second);
            dialog.show(getChildFragmentManager(), null);
        });

        viewModel.dismiss.observe(getViewLifecycleOwner(), (Observer<Object>) o -> dismiss());

        viewModel.deleteBtnVisibility.observe(getViewLifecycleOwner(), visibility ->
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(visibility ? View.VISIBLE : View.GONE));

        RxTextView.textChanges(etSubjectName)
                .skip(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<CharSequence>) charSequence ->
                        charSequence.length() > 2 && etSubjectName.hasFocus())
                .map(CharSequence::toString)
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribe(subjectName -> viewModel.onSubjectNameType(subjectName));

        RxTextView.textChanges(etTeacherName)
                .skip(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<CharSequence>) charSequence ->
                        charSequence.length() > 3 && etTeacherName.hasFocus())
                .map(CharSequence::toString)
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribe(teacherName -> viewModel.onTeacherNameType(teacherName));
        return inflater.inflate(R.layout.dialog_choice_subject_teacher, null);
    }

    private void setPopupMenu(View view, List<ListItem> items, int resourceLayout, OnItemClickListener listener) {
        if (popupWindow != null) popupWindow.dismiss();
        popupWindow = new ListPopupWindow(requireActivity());
        popupWindow.setAnchorView(view);
        ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(getContext(), resourceLayout, items);
        popupWindow.setAdapter(adapter);
        popupWindow.setOnItemClickListener((parent, vsubiew1, position, id) -> {
            popupWindow.dismiss();
            listener.onItemClick(position);
        });
        popupWindow.show();
    }
}
