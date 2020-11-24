package com.example.kts.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kts.CenteredTitleToolbar;
import com.example.kts.R;
import com.example.kts.RxBusChoiceOfGroup;
import com.example.kts.ui.adapters.LoginFragmentsAdapter;
import com.example.kts.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;

import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;

public class LoginActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private ViewPager2 viewpager;
    private LoginFragmentsAdapter adapter;
    private CenteredTitleToolbar toolbar;
    private LoginViewModel viewModel;
    private RichPath linePath;
    private FloatingActionButton fabBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        adapter = new LoginFragmentsAdapter(this);

        viewpager = findViewById(R.id.viewpager_login_pages);
        viewpager.setUserInputEnabled(false);
        toolbar = findViewById(R.id.toolbar_login);
        RichPathView rpProgressAuth = findViewById(R.id.richPathView_progressAuth);
        fabBack = findViewById(R.id.fab_back);
        linePath = rpProgressAuth.findRichPathByName("line");

        if (viewModel.getSavedList() != null) {
            adapter.setData(viewModel.getSavedList());
        } else {
            adapter.addData(LoginViewModel.FragmentType.CHOICE_ROLE);
        }
        viewpager.setAdapter(adapter);

        fabBack.setOnClickListener(view -> {
            hideKeyboard();
            viewModel.onBackPress(viewpager.getCurrentItem());
        });

        RxBusChoiceOfGroup.getInstance().getSelectGroupEvent()
                .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<Object>) o12 -> o12 != RxBusChoiceOfGroup.EMPTY)
                .subscribe(o -> {
            String groupUuid = String.valueOf(o);
            viewModel.onGroupItemClick(groupUuid);
        });

        viewModel.toolbarTitle.observe(this, title -> toolbar.setTitle(title));

        viewModel.currentProgress.observe(this, value -> RichPathAnimator.animate(linePath)
                .trimPathEnd(value)
                .duration(400)
                .interpolator(new FastOutSlowInInterpolator())
                .start());

        viewModel.addFragment.observe(this, this::addFragment);

        viewModel.backToFragment.observe(this, aVoid -> adapter.removeFragment(viewpager.getCurrentItem()));

        viewModel.fabVisibility.observe(this, aBoolean -> {
            if (aBoolean)
                fabBack.show();
            else
                fabBack.hide();
        });

        viewModel.startMainActivity.observe(this, (Observer<Object>) o -> handler.postDelayed(() -> {
            viewpager.setAdapter(null);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 500));

        viewModel.finishApp.observe(this, aVoid -> finish());
    }

    private void addFragment(LoginViewModel.FragmentType fragmentType) {
        adapter.addData(fragmentType);
        int nextFragment = viewpager.getCurrentItem() + 1;
        adapter.notifyItemInserted(nextFragment);
        viewpager.setCurrentItem(nextFragment);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPress(viewpager.getCurrentItem());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.saveList(adapter.getData());
        handler.removeCallbacksAndMessages(null);
    }
}