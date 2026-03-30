package com.melikash98.editify;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomInputEdit extends ConstraintLayout {
    private ConstraintLayout backInput;
    private AppCompatEditText editInput;
    private ConstraintLayout hintLayout;
    private ImageView iconStart;
    private TextView hintTextView;
    private ImageView iconEnd;

    // Validation views
    private ConstraintLayout helperBack, wrongBack, errorBack;
    private ImageView helpIcon, warningIcon, errorIcon;
    private TextView helperText, warningText, errorText;

    private Drawable activeBackground;
    private Drawable inactiveBackground;

    private int inactiveHintColor = Color.parseColor("#BDBDBD");
    private int activeHintColor = Color.parseColor("#000000");

    private boolean isActive = false;

    public CustomInputEdit(@NonNull Context context) {
        this(context, null);
    }

    public CustomInputEdit(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomInputEdit(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_input_field, this, true);

        backInput = findViewById(R.id.backInput);
        editInput = findViewById(R.id.editInput);
        hintLayout = findViewById(R.id.hintLayout);
        iconStart = findViewById(R.id.iconStart);
        hintTextView = findViewById(R.id.hintText);
        iconEnd = findViewById(R.id.iconEnd);

        helperBack = findViewById(R.id.helperBack);
        wrongBack = findViewById(R.id.wrongBack);
        errorBack = findViewById(R.id.errorBack);
        helpIcon = findViewById(R.id.helpIcon);
        warningIcon = findViewById(R.id.warningIcon);
        errorIcon = findViewById(R.id.errorIcon);
        helperText = findViewById(R.id.helperText);
        warningText = findViewById(R.id.warningText);
        errorText = findViewById(R.id.errorText);

        inactiveBackground = context.getDrawable(R.drawable.input_inactive);
        activeBackground = context.getDrawable(R.drawable.input_active);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

            String hintStr = a.getString(R.styleable.CustomInputField_hintText);
            if (!TextUtils.isEmpty(hintStr)) hintTextView.setText(hintStr);

            int hintIconRes = a.getResourceId(R.styleable.CustomInputField_hintIcon, 0);
            if (hintIconRes != 0) iconStart.setImageResource(hintIconRes);

            int endIconRes = a.getResourceId(R.styleable.CustomInputField_endIcon, 0);
            if (endIconRes != 0) iconEnd.setImageResource(endIconRes);

            if (a.hasValue(R.styleable.CustomInputField_hintColor))
                inactiveHintColor = a.getColor(R.styleable.CustomInputField_hintColor, inactiveHintColor);

            if (a.hasValue(R.styleable.CustomInputField_textColor)) {
                activeHintColor = a.getColor(R.styleable.CustomInputField_textColor, activeHintColor);
                editInput.setTextColor(activeHintColor);
            }

            int activeBgRes = a.getResourceId(R.styleable.CustomInputField_activeBackground, 0);
            if (activeBgRes != 0) activeBackground = context.getDrawable(activeBgRes);

            int inactiveBgRes = a.getResourceId(R.styleable.CustomInputField_inactiveBackground, 0);
            if (inactiveBgRes != 0) inactiveBackground = context.getDrawable(inactiveBgRes);

            a.recycle();
        }

        // حالت اولیه
        backInput.setBackground(inactiveBackground);
        setHintAndIconColor(inactiveHintColor);
        editInput.setBackgroundColor(Color.TRANSPARENT);
        editInput.setPadding(editInput.getPaddingLeft(), dpToPx(12), editInput.getPaddingRight(), dpToPx(12));

        setupListeners();

        // اگر متن اولیه داشته باشد، فعال شود
        if (!TextUtils.isEmpty(editInput.getText())) {
            updateInputState(true);
        }
    }

    private void setHintAndIconColor(int color) {
        hintTextView.setTextColor(color);
        iconStart.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void setupListeners() {
        editInput.setOnFocusChangeListener((v, hasFocus) ->
                updateInputState(hasFocus || !TextUtils.isEmpty(editInput.getText())));

        editInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateInputState(editInput.hasFocus() || s.length() > 0);
            }
        });
    }

    private void updateInputState(boolean shouldBeActive) {
        if (isActive == shouldBeActive) return;
        isActive = shouldBeActive;

        if (shouldBeActive) activateInput();
        else deactivateInput();
    }

    private void activateInput() {
        backInput.setBackground(activeBackground);

        // هینت بالا می‌رود و داخل بوردر قرار می‌گیرد
        float translationUp = -dpToPx(28);

        ObjectAnimator translateY = ObjectAnimator.ofFloat(hintLayout, "translationY", 0f, translationUp);

        ValueAnimator colorAnim = ValueAnimator.ofArgb(inactiveHintColor, activeHintColor);
        colorAnim.addUpdateListener(anim -> setHintAndIconColor((int) anim.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateY, colorAnim);
        set.setDuration(220);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        // padding برای جلوگیری از هم‌پوشانی متن
        backInput.setPadding(backInput.getPaddingLeft(), dpToPx(32), backInput.getPaddingRight(), backInput.getPaddingBottom());
        editInput.setPadding(editInput.getPaddingLeft(), dpToPx(34), editInput.getPaddingRight(), editInput.getPaddingBottom());
    }

    private void deactivateInput() {
        backInput.setBackground(inactiveBackground);

        ObjectAnimator translateY = ObjectAnimator.ofFloat(hintLayout, "translationY", hintLayout.getTranslationY(), 0f);

        ValueAnimator colorAnim = ValueAnimator.ofArgb(activeHintColor, inactiveHintColor);
        colorAnim.addUpdateListener(anim -> setHintAndIconColor((int) anim.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateY, colorAnim);
        set.setDuration(200);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        backInput.setPadding(backInput.getPaddingLeft(), dpToPx(12), backInput.getPaddingRight(), backInput.getPaddingBottom());
        editInput.setPadding(editInput.getPaddingLeft(), dpToPx(12), editInput.getPaddingRight(), editInput.getPaddingBottom());
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
