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
    private AppCompatEditText editInput;
    private ConstraintLayout hintLayout;
    private ImageView hintIcon;
    private TextView hintTextView;


    private Drawable activeBackground;
    private Drawable inactiveBackground;
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

        inactiveBackground = context.getDrawable(R.drawable.input_inactive);
        activeBackground = context.getDrawable(R.drawable.input_active);

    }


}
