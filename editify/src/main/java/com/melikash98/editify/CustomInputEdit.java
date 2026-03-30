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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomInputEdit extends ConstraintLayout {
    private AppCompatEditText editInput;
    private ConstraintLayout hintLayout;
    private ImageView hintIcon;
    private TextView hintTextView;


    private Drawable activeBackground;
    private Drawable inactiveBackground;

    private boolean isFocus = false;
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

        editInput = findViewById(R.id.editInput);
        hintLayout = findViewById(R.id.hintLayout);
        hintIcon= findViewById(R.id.iconStart);
        hintTextView = findViewById(R.id.hintText);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);
        hintTextView.setText(array.getString(R.styleable.CustomInputField_hintText));
        if (array.getDrawable(R.styleable.CustomInputField_hintIcon) != null) {
            hintIcon.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_hintIcon));
        }

        activeBackground = array.getDrawable(R.styleable.CustomInputField_activeBackground);
        inactiveBackground = array.getDrawable(R.styleable.CustomInputField_inactiveBackground);
        if (activeBackground == null) activeBackground = context.getDrawable(R.drawable.input_active);
        if (inactiveBackground == null) inactiveBackground = context.getDrawable(R.drawable.input_inactive);

        array.recycle();
        editInput.setBackground(inactiveBackground);
        editInput.setOnFocusChangeListener((v, hasFocus) -> {
            isFocus = hasFocus;
            if (hasFocus) editInput.setBackground(activeBackground);
            updateHintPosition();
        });
        editInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                isActive = s.length() > 0;
                updateHintPosition();
            }
        });
        updateHintPosition();
    }

    private void updateHintPosition() {
        hintLayout.post(() -> {

            boolean shouldFloat = isFocus || isActive;

            float centerY = 0f; // چون با constraint وسطه

            // فاصله از مرکز تا خط بالا
            float distanceToTop = (editInput.getHeight() / 2f) - (hintLayout.getHeight() / 2f);

            // یه مقدار خیلی کم برای اینکه دقیق روی خط بشینه
            float adjust = dp(10);

            float targetY = shouldFloat
                    ? -(distanceToTop + adjust)   // بره روی خط
                    : centerY;                   // برگرده وسط

            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator translateY = ObjectAnimator.ofFloat(
                    hintLayout,
                    "translationY",
                    targetY
            );

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                    hintLayout,
                    "scaleX",
                    shouldFloat ? 0.88f : 1f
            );

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                    hintLayout,
                    "scaleY",
                    shouldFloat ? 0.88f : 1f
            );

            animatorSet.playTogether(translateY, scaleX, scaleY);
            animatorSet.setDuration(220);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
        });
    }
    private float dp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
    public String getText() {
        return editInput.getText().toString().trim();
    }

    public void setText(String text) {
        editInput.setText(text);
    }

}
