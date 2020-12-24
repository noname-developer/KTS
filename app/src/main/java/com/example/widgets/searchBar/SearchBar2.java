package com.example.widgets.searchBar;

import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.kts.CenteredTitleToolbar;
import com.example.kts.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import static com.example.kts.utils.DimensionUtils.dpToPx;

public class SearchBar2 extends LinearLayout {

    private OnQueryTextListener onQueryTextListener;
    private View root;
    private TextView textView;
    private EditText editText;
    private ImageView btnBack;
    private CardView cardView;
    private boolean expanded;
    private InputMethodManager lManager;
    private ViewSwitcher swInputText;
//    private ViewSwitcher swIcons;
    private Toolbar toolbar;

    public SearchBar2(Context context) {
        super(context);
        init(context);
    }

    public SearchBar2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBar2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SearchBar2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NotNull Context context) {
        lManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_bar2, this);

        toolbar = findViewById(R.id.MaterialToolbar);
        root = findViewById(R.id.root);
        swInputText = findViewById(R.id.viewSwitcher_search_input);
//        swIcons = findViewById(R.id.viewSwitcher_search_icon);
        cardView = findViewById(R.id.cardView_search);
        textView = findViewById(R.id.textView_search);
        editText = findViewById(R.id.editText_search);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (onQueryTextListener != null) {
                    onQueryTextListener.onQueryTextSubmit(editText.getText().toString());
                }
                return true;
            }
            return false;
        });

        AppCompatActivity activity = (AppCompatActivity) context;
        activity.setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            Toast.makeText(context, "CLICK", Toast.LENGTH_SHORT).show();
            expanded = !expanded;
            swInputText.showNext();
//            swIcons.showNext();
            setExpanded(expanded);
            if (expanded) {
                editText.requestFocusFromTouch();
                lManager.showSoftInput(editText, 0);
            } else {
//                lManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

    }

    private void setExpanded(boolean expanded) {
        ValueAnimator marginsAnimator, radiusAnimator;
        if (expanded) {
            marginsAnimator = createMarginsAnimator(
                    dpToPx(0, getContext()),
                    dpToPx(0, getContext()),
                    dpToPx(0, getContext()),
                    dpToPx(2, getContext())
            );
            radiusAnimator = createRadiusAnimator(1f);
        } else {
            marginsAnimator = createMarginsAnimator(
                    dpToPx(24, getContext()),
                    dpToPx(24, getContext()),
                    dpToPx(10, getContext()),
                    dpToPx(2, getContext())
            );
            radiusAnimator = createRadiusAnimator(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getContext().getResources().getDisplayMetrics()));
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(marginsAnimator, radiusAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    @NotNull
    private ValueAnimator createMarginsAnimator(int left, int right, int top, int bottom) {
        MarginLayoutParams layoutParams = ((MarginLayoutParams) cardView.getLayoutParams());
        PropertyValuesHolder leftMargin = PropertyValuesHolder.ofInt("left", layoutParams.leftMargin, left);
        PropertyValuesHolder rightMargin = PropertyValuesHolder.ofInt("right", layoutParams.rightMargin, right);
        PropertyValuesHolder topMargin = PropertyValuesHolder.ofInt("top", layoutParams.topMargin, top);
        PropertyValuesHolder bottomMargin = PropertyValuesHolder.ofInt("bottom", layoutParams.bottomMargin, bottom);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(leftMargin, rightMargin, topMargin, bottomMargin);
        valueAnimator.addUpdateListener(animation -> {
            layoutParams.leftMargin = (Integer) valueAnimator.getAnimatedValue("left");
            layoutParams.rightMargin = (Integer) valueAnimator.getAnimatedValue("right");
            layoutParams.topMargin = (Integer) valueAnimator.getAnimatedValue("top");
            layoutParams.bottomMargin = (Integer) valueAnimator.getAnimatedValue("bottom");
            cardView.setLayoutParams(layoutParams);
        });
        return valueAnimator;
    }

    @NotNull
    private ValueAnimator createRadiusAnimator(float radius) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cardView.getRadius(), radius);
        valueAnimator.addUpdateListener(animation -> cardView.setRadius((Float) valueAnimator.getAnimatedValue()));
        return valueAnimator;
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        onQueryTextListener = listener;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onQueryTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface OnQueryTextListener {

        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);
    }

}
