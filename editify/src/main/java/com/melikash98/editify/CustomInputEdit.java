package com.melikash98.editify;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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

    private Drawable activeBackground;
    private Drawable inactiveBackground;

    private int inactiveHintColor;
    private int activeHintColor;

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
        inflate(context, R.layout.custom_input_field, this);

        backInput = findViewById(R.id.backInput);
        editInput = findViewById(R.id.editInput);
        hintLayout = findViewById(R.id.hintLayout);
        iconStart = findViewById(R.id.iconStart);
        hintTextView = findViewById(R.id.hintText);
        iconEnd = findViewById(R.id.iconEnd);

        // مقادیر پیش‌فرض از attrs.xml
        inactiveBackground = context.getDrawable(R.drawable.input_inactive);
        activeBackground = context.getDrawable(R.drawable.input_active);

        inactiveHintColor = Color.parseColor("#BDBDBD"); // رنگ hint غیرفعال
        activeHintColor = Color.parseColor("#000000");   // رنگ hint وقتی بالا میره

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

            // hint متن
            String hintStr = a.getString(R.styleable.CustomInputField_hintText);
            if (!TextUtils.isEmpty(hintStr)) {
                hintTextView.setText(hintStr);
            }

            // آیکون hint
            int hintIconRes = a.getResourceId(R.styleable.CustomInputField_hintIcon, 0);
            if (hintIconRes != 0) {
                iconStart.setImageResource(hintIconRes);
            }

            // آیکون انتها (فعلاً فقط visibility)
            int endIconRes = a.getResourceId(R.styleable.CustomInputField_endIcon, 0);
            if (endIconRes != 0) {
                iconEnd.setImageResource(endIconRes);
            }

            // رنگ hint (غیرفعال)
            if (a.hasValue(R.styleable.CustomInputField_hintColor)) {
                inactiveHintColor = a.getColor(R.styleable.CustomInputField_hintColor, inactiveHintColor);
            }

            // رنگ متن (برای hint فعال و متن داخل EditText)
            if (a.hasValue(R.styleable.CustomInputField_textColor)) {
                activeHintColor = a.getColor(R.styleable.CustomInputField_textColor, activeHintColor);
                editInput.setTextColor(activeHintColor);
            }

            // بک‌گراندهای فعال و غیرفعال
            int activeBgRes = a.getResourceId(R.styleable.CustomInputField_activeBackground, 0);
            if (activeBgRes != 0) {
                activeBackground = context.getDrawable(activeBgRes);
            }
            int inactiveBgRes = a.getResourceId(R.styleable.CustomInputField_inactiveBackground, 0);
            if (inactiveBgRes != 0) {
                inactiveBackground = context.getDrawable(inactiveBgRes);
            }

            a.recycle();
        }

        // حالت اولیه (غیرفعال)
        backInput.setBackground(inactiveBackground);
        hintTextView.setTextColor(inactiveHintColor);
        iconStart.setColorFilter(inactiveHintColor);
        editInput.setBackgroundColor(Color.TRANSPARENT);

        setupListeners();
    }

    private void setupListeners() {
        // listener فوکوس
        editInput.setOnFocusChangeListener((v, hasFocus) -> {
            boolean shouldBeActive = hasFocus || !TextUtils.isEmpty(editInput.getText());
            updateInputState(shouldBeActive);
        });

        // listener تغییر متن (اگر متن وارد شد حتی بدون فوکوس فعال بماند)
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean shouldBeActive = editInput.hasFocus() || s.length() > 0;
                updateInputState(shouldBeActive);
            }
        });
    }

    private void updateInputState(boolean shouldBeActive) {
        if (isActive == shouldBeActive) return;
        isActive = shouldBeActive;

        if (shouldBeActive) {
            activateInput();
        } else {
            deactivateInput();
        }
    }

    private void activateInput() {
        // تغییر بک‌گراند به حالت فعال (dashed)
        backInput.setBackground(activeBackground);

        // انیمیشن: hint به بالا + تغییر رنگ
        float translationUp = -dpToPx(26); // دقیقاً مطابق ویدیو (hint بره بالا)

        ObjectAnimator translateY = ObjectAnimator.ofFloat(hintLayout, "translationY", 0f, translationUp);
        ObjectAnimator colorHint = ObjectAnimator.ofArgb(hintTextView, "textColor", inactiveHintColor, activeHintColor);
        ObjectAnimator colorIcon = ObjectAnimator.ofArgb(iconStart, "colorFilter", inactiveHintColor, activeHintColor);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateY, colorHint, colorIcon);
        set.setDuration(220);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        // افزایش padding بالای EditText تا متن با hint تداخل نداشته باشه
        int newTopPadding = dpToPx(28);
        editInput.setPadding(editInput.getPaddingLeft(), newTopPadding, editInput.getPaddingRight(), editInput.getPaddingBottom());
    }

    private void deactivateInput() {
        // تغییر بک‌گراند به حالت غیرفعال
        backInput.setBackground(inactiveBackground);

        // انیمیشن: hint به پایین + تغییر رنگ به حالت اولیه
        float translationDown = 0f;

        ObjectAnimator translateY = ObjectAnimator.ofFloat(hintLayout, "translationY", hintLayout.getTranslationY(), translationDown);
        ObjectAnimator colorHint = ObjectAnimator.ofArgb(hintTextView, "textColor", activeHintColor, inactiveHintColor);
        ObjectAnimator colorIcon = ObjectAnimator.ofArgb(iconStart, "colorFilter", activeHintColor, inactiveHintColor);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateY, colorHint, colorIcon);
        set.setDuration(200);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        // بازگرداندن padding EditText
        editInput.setPadding(editInput.getPaddingLeft(), dpToPx(10), editInput.getPaddingRight(), editInput.getPaddingBottom());
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // متدهای عمومی ساده (فقط برای دسترسی به EditText)
    public AppCompatEditText getEditText() {
        return editInput;
    }

    public String getText() {
        return editInput.getText() != null ? editInput.getText().toString().trim() : "";
    }

    public void setText(String text) {
        editInput.setText(text);
    }
}
