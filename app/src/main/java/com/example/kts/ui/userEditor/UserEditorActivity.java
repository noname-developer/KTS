package com.example.kts.ui.userEditor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.avatarGenerator.AvatarGenerator;
import com.example.kts.AsyncTransformer;
import com.example.kts.AvatarBuilderTemplate;
import com.example.kts.CenteredTitleToolbar;
import com.example.kts.InstantAutoComplete;
import com.example.kts.KeyboardManager;
import com.example.kts.R;
import com.example.kts.customPopup.ListPopupWindowAdapter;
import com.example.kts.ui.confirm.ConfirmDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.io.ByteArrayOutputStream;

public class UserEditorActivity extends AppCompatActivity {

    public static final String USER_ROLE = "USER_TYPE";
    public static final String USER_UUID = "USER_UUID";
    public static final String USER_GROUP_UUID = "GROUP_UUID";
    public static final int DELAY_FAB_VISIBILITY = 300;
    private UserEditorViewModel viewModel;
    private ImageView ivAvatar;
    private EditText etFirstName, etSecondName, etPhoneNum;
    private InstantAutoComplete etGender, etRole, etGroup;
    private KeyboardManager KeyboardManager;
    private ExtendedFloatingActionButton fab;
    private Menu menu;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_editor);
        UserEditorViewModelFactory factory = new UserEditorViewModelFactory(
                getApplication(),
                getIntent().getStringExtra(USER_UUID),
                getIntent().getStringExtra(USER_ROLE),
                getIntent().getStringExtra(USER_GROUP_UUID));
        viewModel = new ViewModelProvider(this, factory).get(UserEditorViewModel.class);
        KeyboardManager = new KeyboardManager(this);

        ivAvatar = findViewById(R.id.imageView_avater);
        etFirstName = findViewById(R.id.editText_firstName);
        etSecondName = findViewById(R.id.editText_secondName);
        etGender = findViewById(R.id.editText_gender);
        etRole = findViewById(R.id.editText_role);
        etPhoneNum = findViewById(R.id.editText_phoneNum);
        etGroup = findViewById(R.id.editText_group);
        fab = findViewById(R.id.fab);

        etGender.inputEnable(false);
        etRole.inputEnable(false);
        etPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        CenteredTitleToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final UserEditorActivity context = UserEditorActivity.this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.onFabClick();
            }
        });

        RxTextView.textChanges(etFirstName)
                .skip(2)
                .compose(new AsyncTransformer<>())
                .map(CharSequence::toString)
                .subscribe(s -> viewModel.onFirstNameType(s));

        RxTextView.textChanges(etSecondName)
                .skip(2)
                .compose(new AsyncTransformer<>())
                .map(CharSequence::toString)
                .subscribe(s -> viewModel.onSecondNameType(s));

        RxTextView.textChanges(etPhoneNum)
                .compose(new AsyncTransformer<>())
                .map(CharSequence::toString)
                .subscribe(s -> viewModel.onPhoneNumType(s));

        RxTextView.textChanges(etGroup)
                .skip(2)
                .compose(new AsyncTransformer<>())
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty())
                .subscribe(s -> viewModel.onGroupNameType(s));

        viewModel.toolbarTitle.observe(this, this::setTitle);

        viewModel.fieldFirstName.observe(this, role -> etFirstName.setText(role));

        viewModel.fieldSecondName.observe(this, role -> etSecondName.setText(role));

        viewModel.fieldPhoneNum.observe(this, role -> etPhoneNum.setText(role));

        viewModel.fieldGender.observe(this, gender -> etGender.setText(gender));

        viewModel.fieldRole.observe(this, groupName -> etRole.setText(groupName));

        viewModel.fieldGroup.observe(this, groupName -> etGroup.setText(groupName));

        viewModel.fabIconAndText.observe(this, iconWithTextPair -> {
            fab.setIcon(ContextCompat.getDrawable(UserEditorActivity.this, iconWithTextPair.first));
            fab.setText(iconWithTextPair.second);
        });

        viewModel.deleteOptionVisibility.observe(this, visibility -> menu.findItem(R.id.option_delete_user).setVisible(visibility));

        viewModel.generateAvatar.observe(this, name -> {
            BitmapDrawable avatar = new AvatarGenerator.Builder(context, name)
                    .initFrom(new AvatarBuilderTemplate())
                    .generate();

            Glide.with(this)
                    .load(avatar)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivAvatar);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] avatarBytes = stream.toByteArray();
            viewModel.onAvatarGenerate(avatarBytes);
        });

        viewModel.avatarUser.observe(this, photoUrl -> Glide.with(UserEditorActivity.this)
                .load(photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(ivAvatar));

        viewModel.fieldErrorMessage.observe(this, idWithMessagePair -> {
            TextInputLayout textInputLayout = findViewById(idWithMessagePair.first);
            if (idWithMessagePair != null) {
                textInputLayout.setError(idWithMessagePair.second);
            } else {
                textInputLayout.setErrorEnabled(false);
            }
        });

        viewModel.fieldRoleList.observe(this, items -> {
            ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(context, R.layout.item_popup_content, items);
            etRole.setAdapter(adapter);
            etRole.setOnItemClickListener((parent, view, position, id) -> {
                if (adapter.getItem(position).isEnabled()) {
                    viewModel.onRoleSelect(position);
                }
            });
        });

        viewModel.fieldGenderList.observe(this, items -> {
            ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(context, R.layout.item_popup_content, items);
            etGender.setAdapter(adapter);
            etGender.setOnItemClickListener((parent, view, position, id) -> viewModel.onGenderSelect(position));
        });

        viewModel.groupList.observe(this, items -> {
            ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(context, R.layout.item_popup_content, items);
            etGroup.setAdapter(adapter);
            etGroup.setOnItemClickListener((parent, view, position, id) -> {
                if (adapter.getItem(position).isEnabled()) {
                    viewModel.onGroupSelect(position);
                }
            });
        });

        viewModel.finish.observe(this, (Observer<Object>) o -> finish());

        viewModel.openConfirmation.observe(this, titleWithSubtitlePair -> {
            ConfirmDialog dialog = ConfirmDialog.newInstance(titleWithSubtitlePair.first, titleWithSubtitlePair.second);
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_editor_options, menu);
        this.menu = menu;
        viewModel.onCreateOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            viewModel.onOptionSelect(item.getItemId());
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyboardManager.registerKeyboardListener(new KeyboardManager.OnKeyboardListener() {
            @Override
            public void onKeyboardVisible() {
                fab.postDelayed(() -> fab.hide(), DELAY_FAB_VISIBILITY);
                Toast.makeText(UserEditorActivity.this, "SHOW", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKeyboardHidden() {
                fab.postDelayed(() -> fab.show(), DELAY_FAB_VISIBILITY);
                Toast.makeText(UserEditorActivity.this, "HIDE", Toast.LENGTH_SHORT).show();
            }
        }, findViewById(android.R.id.content));
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardManager.unregisterKeyboardListener();
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPress();
    }
}