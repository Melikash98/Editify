package com.melikash98.editify;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class CustomInputEdit extends ConstraintLayout {
    private AppCompatEditText editInput;
    private ConstraintLayout hintLayout;
    private ConstraintLayout containerLayout;
    private ImageView hintIcon;
    private TextView hintTextView;
    private ImageView iconPass;
    private ImageView helperIconView;
    private TextView helperTextView;
    private ImageView warningIconView;
    private TextView warningTextView;
    private ImageView errorIconView;
    private TextView errorTextView;

    private Drawable activeBackground;
    private Drawable inactiveBackground;

    private Drawable passShowDrawable;
    private Drawable passHideDrawable;

    private int hintDefaultColor;
    private int hintActiveColor;
    private int passIconColor;
    private int helperColor;
    private int warningColor;
    private int errorColor;
    private float helperTextSize;
    private String helperTextFamily;
    private int helperFontResId;
    private int helperTextStyle;

    private boolean isFocus = false;
    private boolean isActive = false;
    private boolean isRightDirection = false;
    private boolean isPasswordVisible = false;


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
        hintIcon = findViewById(R.id.iconStart);
        hintTextView = findViewById(R.id.hintText);
        containerLayout = findViewById(R.id.containerLayout);
        iconPass = findViewById(R.id.iconPass);
        helperIconView = findViewById(R.id.helperIcon);
        helperTextView = findViewById(R.id.helperText);
        warningIconView = findViewById(R.id.warningIcon);
        warningTextView = findViewById(R.id.warningText);
        errorIconView = findViewById(R.id.errorIcon);
        errorTextView = findViewById(R.id.errorText);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);
        hintTextView.setText(array.getString(R.styleable.CustomInputField_hintText));
        if (array.getDrawable(R.styleable.CustomInputField_hintIcon) != null) {
            hintIcon.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_hintIcon));
        }
        float hintSize = array.getDimension(R.styleable.CustomInputField_hintSize, 0);
        if (hintSize > 0) {
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize);
        }
        String hintFamily = array.getString(R.styleable.CustomInputField_hintFamily);
        if (!TextUtils.isEmpty(hintFamily)) {
            Typeface hintTypeface = Typeface.create(hintFamily, Typeface.NORMAL);
            hintTextView.setTypeface(hintTypeface);
        }
        float inputSize = array.getDimension(R.styleable.CustomInputField_inputSize, 0);
        if (inputSize > 0) {
            editInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputSize);
        }
        String inputFamily = array.getString(R.styleable.CustomInputField_inputFamily);
        if (!TextUtils.isEmpty(inputFamily)) {
            Typeface inputTypeface = Typeface.create(inputFamily, Typeface.NORMAL);
            editInput.setTypeface(inputTypeface);
        }
        String inputText = array.getString(R.styleable.CustomInputField_input);
        if (!TextUtils.isEmpty(inputText)) {
            editInput.setText(inputText);
        }
        int editTextColor = array.getColor(R.styleable.CustomInputField_inputColor,
                array.getColor(R.styleable.CustomInputField_textColor, Color.BLACK));
        editInput.setTextColor(editTextColor);
        isRightDirection = array.getBoolean(R.styleable.CustomInputField_rightDirection, false);
        hintDefaultColor = array.getColor(
                R.styleable.CustomInputField_hintColor,
                hintTextView.getCurrentTextColor()
        );
        hintActiveColor = array.getColor(
                R.styleable.CustomInputField_hintActiveColor,
                hintDefaultColor
        );

        int hintBgColor = array.getColor(R.styleable.CustomInputField_hintBackgroundColor, Color.WHITE);
        Drawable hintBackground = context.getDrawable(R.drawable.hint_bg); // shape اصلی
        if (hintBackground instanceof GradientDrawable) {
            ((GradientDrawable) hintBackground.mutate()).setColor(hintBgColor);
        } else {
            hintBackground.setTint(hintBgColor);
        }
        hintLayout.setBackground(hintBackground);
        activeBackground = array.getDrawable(R.styleable.CustomInputField_activeBackground);
        inactiveBackground = array.getDrawable(R.styleable.CustomInputField_inactiveBackground);
        if (activeBackground == null)
            activeBackground = context.getDrawable(R.drawable.input_active);
        if (inactiveBackground == null)
            inactiveBackground = context.getDrawable(R.drawable.input_inactive);

        passShowDrawable = array.getDrawable(R.styleable.CustomInputField_passShow);
        passHideDrawable = array.getDrawable(R.styleable.CustomInputField_passHide);
        passIconColor = array.getColor(R.styleable.CustomInputField_passIconColor, Color.GRAY);
        int inputType = array.getInt(R.styleable.CustomInputField_inputType,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        array.recycle();

        if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)) {
            TypedArray androidArray = context.obtainStyledAttributes(attrs,
                    new int[]{android.R.attr.inputType});
            inputType = androidArray.getInt(0, inputType);
            androidArray.recycle();
        }
        editInput.setInputType(inputType);

        setupDirectionConstraints();

        editInput.setBackground(inactiveBackground);
        editInput.setOnFocusChangeListener((v, hasFocus) -> {
            isFocus = hasFocus;
            if (hasFocus) editInput.setBackground(activeBackground);
            updateUIState();
        });
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isActive = s.length() > 0;
                updateUIState();
            }
        });

        setupPasswordToggle();

        hintLayout.bringToFront();
        iconPass.bringToFront();
        post(this::updateUIState);
    }

    private void setupPasswordToggle() {
        if (passShowDrawable == null || passHideDrawable == null) return;

        int currentType = editInput.getInputType();
        boolean isPassword = (currentType & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0 ||
                (currentType & InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) != 0 ||
                (currentType & InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0;

        if (isPassword) {
            iconPass.setVisibility(View.VISIBLE);
            iconPass.setImageDrawable(passHideDrawable);
            iconPass.setColorFilter(passIconColor, PorterDuff.Mode.SRC_IN);
            iconPass.setOnClickListener(v -> togglePasswordVisibility());
        } else {
            iconPass.setVisibility(View.GONE);
        }
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            editInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            iconPass.setImageDrawable(passShowDrawable);
        } else {
            editInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            iconPass.setImageDrawable(passHideDrawable);
        }
        iconPass.setColorFilter(passIconColor, PorterDuff.Mode.SRC_IN);
        editInput.setSelection(editInput.getText().length());
    }

    private void setupDirectionConstraints() {
        ConstraintSet containerSet = new ConstraintSet();
        containerSet.clone(containerLayout);

        containerSet.clear(R.id.hintLayout, ConstraintSet.START);
        containerSet.clear(R.id.hintLayout, ConstraintSet.END);

        if (isRightDirection) {
            containerSet.connect(R.id.hintLayout, ConstraintSet.END, R.id.editInput, ConstraintSet.END, (int) dp(10));
        } else {
            containerSet.connect(R.id.hintLayout, ConstraintSet.START, R.id.editInput, ConstraintSet.START, (int) dp(10));
        }

        containerSet.connect(R.id.hintLayout, ConstraintSet.TOP, R.id.editInput, ConstraintSet.TOP, 0);
        containerSet.connect(R.id.hintLayout, ConstraintSet.BOTTOM, R.id.editInput, ConstraintSet.BOTTOM, 0);
        containerSet.applyTo(containerLayout);

        ConstraintSet hintSet = new ConstraintSet();
        hintSet.clone(hintLayout);

        hintSet.clear(R.id.iconStart, ConstraintSet.START);
        hintSet.clear(R.id.iconStart, ConstraintSet.END);
        hintSet.clear(R.id.hintText, ConstraintSet.START);
        hintSet.clear(R.id.hintText, ConstraintSet.END);

        if (isRightDirection) {
            hintSet.connect(R.id.iconStart, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            hintSet.connect(R.id.hintText, ConstraintSet.END, R.id.iconStart, ConstraintSet.START, (int) dp(10));
        } else {
            hintSet.connect(R.id.iconStart, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            hintSet.connect(R.id.hintText, ConstraintSet.START, R.id.iconStart, ConstraintSet.END, (int) dp(10));
        }
        hintSet.connect(R.id.hintText, ConstraintSet.TOP, R.id.iconStart, ConstraintSet.TOP, 0);
        hintSet.connect(R.id.iconStart, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        hintSet.connect(R.id.iconStart, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        hintSet.applyTo(hintLayout);

        if (isRightDirection) {
            editInput.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            editInput.setTextDirection(View.TEXT_DIRECTION_RTL);
        } else {
            editInput.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            editInput.setTextDirection(View.TEXT_DIRECTION_LTR);
        }

        if (isRightDirection) {
            hintLayout.setPadding((int) dp(30), 0, 10, 0);
        } else {
            hintLayout.setPadding(10, 0, (int) dp(30), 0);
        }
    }

    private void updateUIState() {
        boolean shouldActivate = isFocus || isActive;
        editInput.setBackground(shouldActivate ? activeBackground : inactiveBackground);
        updateHintAppearance(shouldActivate);
        updateHintPosition();
    }

    private void updateHintAppearance(boolean shouldActivate) {
        int targetColor = shouldActivate ? hintActiveColor : hintDefaultColor;

        hintTextView.setTextColor(targetColor);
        if (hintIcon.getDrawable() != null) {
            hintIcon.setColorFilter(targetColor, PorterDuff.Mode.SRC_IN);
        }
    }

    private void updateHintPosition() {
        hintLayout.post(() -> {
            boolean shouldFloat = isFocus || isActive;

            float targetY;
            if (shouldFloat) {
                float centeredTop = (editInput.getHeight() - hintLayout.getHeight()) / 2f;
                float overlap = dp(22);
                targetY = -(centeredTop + overlap);
            } else {
                targetY = 0f;
            }

            hintLayout.animate().cancel();
            hintLayout.animate()
                    .translationY(targetY)
                    .scaleX(shouldFloat ? 0.85f : 1f)
                    .scaleY(shouldFloat ? 0.85f : 1f)
                    .scaleY(1f)
                    .setDuration(220)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
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
    public void setHelperText(String text) {
        if (helperTextView != null) {
            helperTextView.setText(text != null ? text : "");
            helperTextView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    public void setWarningText(String text) {
        if (warningTextView != null) {
            warningTextView.setText(text != null ? text : "");
            warningTextView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    public void setErrorText(String text) {
        if (errorTextView != null) {
            errorTextView.setText(text != null ? text : "");
            errorTextView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }
}
