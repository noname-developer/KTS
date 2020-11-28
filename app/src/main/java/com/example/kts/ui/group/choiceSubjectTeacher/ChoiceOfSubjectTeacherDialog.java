package com.example.kts.ui.group.choiceSubjectTeacher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.kts.R;
import com.example.kts.data.model.entity.Subject;
import com.example.kts.data.model.entity.User;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChoiceOfSubjectTeacherDialog extends DialogFragment {

    private ImageView ivTeacherAvatar, ivSubjectIcon;
    private EditText etTeacherName, etSubjectName;
    private TextView tvTeacherName, tvSubjectName;
    private TextInputLayout tlTeacherName, tlSubjectName;
    private ChoiceOfSubjectTeacherViewModel viewModel;
    private AlertDialog alertDialog;
    private InputMethodManager imm;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
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

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Выберите предмет и преподавателя")
                .setView(view)
                .setPositiveButton("Ок", (dialogInterface, i) -> {
                    viewModel.onPositiveClick();
                });
        alertDialog = dialog.create();
        return alertDialog;
    }

    private void setPopMenuFoundedSubjects(@NotNull List<Subject> subjects) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), etSubjectName);
        enablePopupIcons(popupMenu);
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            MenuItem item = popupMenu.getMenu().add(0, 0, i, subject.getName());
            Glide.with(getActivity()).asBitmap().load(subject.getIconUrl()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    item.setIcon(new BitmapDrawable(getResources(), resource));
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    Log.d("lol", "onLoadCleared: ");
                }
            });
        }
        if (!subjects.isEmpty()) {
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                int order = item.getOrder();
                viewModel.onSubjectSelect(subjects.get(order));
                return false;
            });
            etSubjectName.requestFocus();
        }
    }

    private void setPopMenuFoundedTeachers(@NotNull List<User> teachers) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), etTeacherName);
        enablePopupIcons(popupMenu);
        for (int i = 0; i < teachers.size(); i++) {
            User user = teachers.get(i);
            MenuItem item = popupMenu.getMenu().add(0, 0, i, user.getFullName());
            Glide.with(getActivity()).asBitmap().load(user.getPhotoUrl()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    item.setIcon(new BitmapDrawable(getResources(), resource));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }
        if (!teachers.isEmpty()) {
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                int order = item.getOrder();
                viewModel.onTeacherSelect(teachers.get(order));
                return false;
            });
            etTeacherName.requestFocus();
        }
    }

    private void enablePopupIcons(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ChoiceOfSubjectTeacherViewModel.class);
        return inflater.inflate(R.layout.dialog_choice_subject_teacher, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.enablePositiveBtn.observe(getViewLifecycleOwner(), enabled ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled));

        viewModel.editTextSubjectNameVisibility.observe(getViewLifecycleOwner(), visible -> {
            tlSubjectName.setVisibility( visible ? View.VISIBLE : View.GONE);
            tvSubjectName.setVisibility( visible ? View.GONE : View.VISIBLE);
            if (visible) {
                etSubjectName.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                imm.hideSoftInputFromWindow(etSubjectName.getWindowToken(), 0);
            }
        });
        viewModel.editTextTeacherNameVisibility.observe(getViewLifecycleOwner(), visible -> {
            tlTeacherName.setVisibility( visible ? View.VISIBLE : View.GONE);
            tvTeacherName.setVisibility( visible ? View.GONE : View.VISIBLE);
            if (visible) {
                etTeacherName.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            } else {
                imm.hideSoftInputFromWindow(etTeacherName.getWindowToken(), 0);
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

        viewModel.foundedUsers.observe(getViewLifecycleOwner(), this::setPopMenuFoundedTeachers);

        viewModel.foundedSubjects.observe(getViewLifecycleOwner(), this::setPopMenuFoundedSubjects);

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
    }
}
