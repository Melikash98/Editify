package com.melikash98.editify;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
    private TextView hintText;
    private ImageView iconEnd;


    private boolean isActive = false;
    private boolean isPasswordVisible = false;

    private String hintStr;
    private Drawable activeBackground;
    private Drawable inactiveBackground;
    private int inactiveHintColor;
    private int activeHintColor;
    private boolean showPasswordToggle;

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

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_input_field, this, true);
        backInput = findViewById(R.id.backInput);
        editInput = findViewById(R.id.editInput);
        hintLayout = findViewById(R.id.hintLayout);
        iconStart = findViewById(R.id.iconStart);
        hintText = findViewById(R.id.hintText);
        iconEnd = findViewById(R.id.iconEnd);

        backInput.setClipChildren(false);
        setClipChildren(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

        hintStr = a.getString(R.styleable.CustomInputField_hintText);
        if (hintStr != null) hintText.setText(hintStr);

        activeBackground = a.getDrawable(R.styleable.CustomInputField_activeBackground);
        inactiveBackground = a.getDrawable(R.styleable.CustomInputField_inactiveBackground);

        if (inactiveBackground != null) {
            backInput.setBackground(inactiveBackground);
        } else {
            backInput.setBackgroundResource(R.drawable.input_inactive);
        }

        inactiveHintColor = a.getColor(R.styleable.CustomInputField_hintColor, Color.parseColor("#BDBDBD"));
        activeHintColor = a.getColor(R.styleable.CustomInputField_textColor, Color.BLACK);

        hintText.setTextColor(inactiveHintColor);
        editInput.setTextColor(a.getColor(R.styleable.CustomInputField_textColor, Color.BLACK));

        Drawable startIcon = a.getDrawable(R.styleable.CustomInputField_hintIcon);
        if (startIcon != null) {
            iconStart.setImageDrawable(startIcon);
        }
        Drawable endIcon = a.getDrawable(R.styleable.CustomInputField_endIcon);
        if (endIcon != null) {
            iconEnd.setImageDrawable(endIcon);
            iconEnd.setVisibility(VISIBLE);
        }

        a.recycle();

        setupListeners();
        updateState(false);
    }
    private void setupListeners() {
        editInput.setOnFocusChangeListener((v, hasFocus) -> {
            boolean shouldBeActive = hasFocus || !TextUtils.isEmpty(editInput.getText());
            updateState(shouldBeActive);
        });
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean shouldBeActive = editInput.hasFocus() || !TextUtils.isEmpty(s);
                updateState(shouldBeActive);
            }
        });
       
    }
    private void updateState(boolean active) {
        if (isActive == active) return;
        isActive = active;

        if (active) {
            activateInput();
        } else {
            deactivateInput();
        }
    }

    private void deactivateInput() {
        if (inactiveBackground != null) {
            backInput.setBackground(inactiveBackground);
        } else {
            backInput.setBackgroundResource(R.drawable.input_inactive);
        }

        animateHintToInactive();
    }

    private void animateHintToInactive() {
        hintLayout.animate()
                .translationY(0)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(220)
                .setInterpolator(new android.view.animation.AccelerateInterpolator())
                .start();

        animateColor(hintText, activeHintColor, inactiveHintColor, 220);
    }

    private void activateInput() {
        if (activeBackground != null) {
            backInput.setBackground(activeBackground);
        } else {
            backInput.setBackgroundResource(R.drawable.input_active);
        }
        animateHintToActive();
    }

    private void animateHintToActive() {
        float density = getResources().getDisplayMetrics().density;
        float targetTranslationY = -36f * density;
        hintLayout.animate()
                .translationY(targetTranslationY)
                .scaleX(0.82f)
                .scaleY(0.82f)
                .setDuration(260)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        animateColor(hintText, inactiveHintColor, activeHintColor, 260);
    }

    private void animateColor(TextView view, int fromColor, int toColor, int duration) {
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnim.setDuration(duration);
        colorAnim.addUpdateListener(animation -> view.setTextColor((int) animation.getAnimatedValue()));
        colorAnim.start();
    }
    public String getText() {
        return editInput.getText() != null ? editInput.getText().toString() : "";
    }

    public void setText(String text) {
        editInput.setText(text);
        updateState(!TextUtils.isEmpty(text));
    }

    public void setHint(String hint) {
        hintStr = hint;
        hintText.setText(hint);
    }

    public AppCompatEditText getEditText() {
        return editInput;
    }

    public void setError(String error) {
    }

    public void setHelperText(String helper) {
    }
}
