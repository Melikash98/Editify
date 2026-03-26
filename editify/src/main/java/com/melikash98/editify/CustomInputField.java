package com.melikash98.editify;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CustomInputField extends LinearLayout {
    private View inputContainer;
    private ImageView startIcon, endIcon, helperIcon;
    private EditText editText;
    private View helperLayout;
    private TextView helperText;

    private boolean isPassword = false;
    private boolean passwordVisible = false;
    private int passwordToggleIconRes = 0;

    public enum HelperType { NONE, ERROR, SUCCESS }

    public CustomInputField(Context context) {
        this(context, null);
    }

    public CustomInputField(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomInputField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.custom_input_field, this, true);

        // پیدا کردن ویوها
        inputContainer = findViewById(R.id.input_container);
        startIcon = findViewById(R.id.start_icon);
        endIcon = findViewById(R.id.end_icon);
        editText = findViewById(R.id.edit_text);
        helperLayout = findViewById(R.id.helper_layout);
        helperIcon = findViewById(R.id.helper_icon);
        helperText = findViewById(R.id.helper_text);

        parseAttributes(attrs);
        setupFocusListener();
        setupPasswordToggle();
        setupRTLSupport();
    }

    private void parseAttributes(@Nullable AttributeSet attrs) {
        if (attrs == null) return;

        var ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomInputField);

        // hint و متن
        editText.setHint(ta.getString(R.styleable.CustomInputField_hint));
        editText.setText(ta.getString(R.styleable.CustomInputField_text));

        // آیکون‌ها
        int startIconRes = ta.getResourceId(R.styleable.CustomInputField_startIcon, 0);
        if (startIconRes != 0) startIcon.setImageResource(startIconRes);

        int endIconRes = ta.getResourceId(R.styleable.CustomInputField_endIcon, 0);
        if (endIconRes != 0) endIcon.setImageResource(endIconRes);

        // نوع ورودی
        int inputTypeEnum = ta.getInt(R.styleable.CustomInputField_inputType, 1);
        switch (inputTypeEnum) {
            case 2: // email
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 3: // password
                isPassword = true;
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 4: // number
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 5: // phone
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }

        // رنگ‌ها
        editText.setHintTextColor(ta.getColor(R.styleable.CustomInputField_hintColor,
                ContextCompat.getColor(getContext(), R.color.gray)));
        editText.setTextColor(ta.getColor(R.styleable.CustomInputField_textColor,
                ContextCompat.getColor(getContext(), R.color.black)));

        int iconColor = ta.getColor(R.styleable.CustomInputField_iconColor,
                ContextCompat.getColor(getContext(), R.color.gray));
        startIcon.setColorFilter(iconColor);
        endIcon.setColorFilter(iconColor);

        // فونت و اندازه
        String fontFamily = ta.getString(R.styleable.CustomInputField_fontFamily);
        if (fontFamily != null) {
            editText.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
        }
        float textSize = ta.getDimension(R.styleable.CustomInputField_textSize, 0);
        if (textSize > 0) {
            editText.setTextSize(textSize / getResources().getDisplayMetrics().scaledDensity);
        }

        // alignment
        int alignment = ta.getInt(R.styleable.CustomInputField_textAlignment, 0);
        if (alignment == 1) editText.setGravity(Gravity.CENTER);
        else if (alignment == 2) editText.setGravity(Gravity.END);

        // background
        int activeBg = ta.getResourceId(R.styleable.CustomInputField_activeBackground, R.drawable.input_active);
        int inactiveBg = ta.getResourceId(R.styleable.CustomInputField_inactiveBackground, R.drawable.input_inactive);
        inputContainer.setBackground(ContextCompat.getDrawable(getContext(), inactiveBg));
        inputContainer.setTag(new int[]{activeBg, inactiveBg});

        passwordToggleIconRes = ta.getResourceId(R.styleable.CustomInputField_passwordToggleIcon, 0);

        ta.recycle();
    }

    private void setupFocusListener() {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            int[] bgIds = (int[]) inputContainer.getTag();
            inputContainer.setBackground(ContextCompat.getDrawable(getContext(),
                    hasFocus ? bgIds[0] : bgIds[1]));
        });
    }

    private void setupPasswordToggle() {
        if (!isPassword) return;

        endIcon.setVisibility(VISIBLE);
        endIcon.setImageResource(passwordToggleIconRes != 0 ? passwordToggleIconRes : android.R.drawable.ic_menu_view);

        endIcon.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                passwordVisible = !passwordVisible;
                editText.setTransformationMethod(passwordVisible ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                editText.setSelection(editText.getText().length());
            }
            return true;
        });
    }

    private void setupRTLSupport() {
        if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
            editText.setGravity(Gravity.END);
        }
    }

    // ====================== API ساده و راحت (دقیقاً همان چیزی که خواستی) ======================

    /** فقط این متد رو صدا کن — همه چیز خودش مدیریت می‌شه */
    public void validate() {
        clearHelper();
        String text = getText();

        if (text.isEmpty()) {
            showError("این فیلد الزامی است");
        } else if (isEmailType() && !isValidEmail()) {
            showError("Invalid Email Address");
        } else {
            showSuccess(isEmailType() ? "Email is OK" : "Name is OK");
        }
    }

    public void showError(@NonNull String message) {
        setHelper(message, HelperType.ERROR, R.drawable.ic_error);
    }

    public void showSuccess(@NonNull String message) {
        setHelper(message, HelperType.SUCCESS, R.drawable.ic_success);
    }

    public void clearHelper() {
        helperLayout.setVisibility(GONE);
    }

    public String getText() {
        return editText.getText().toString().trim();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    private boolean isEmailType() {
        return (editText.getInputType() & InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0;
    }

    private boolean isValidEmail() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(getText()).matches();
    }

    private void setHelper(String text, HelperType type, @DrawableRes int iconRes) {
        helperLayout.setVisibility(VISIBLE);
        helperText.setText(text);

        if (type == HelperType.ERROR) {
            helperText.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            helperIcon.setImageResource(iconRes);
            helperIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
        } else if (type == HelperType.SUCCESS) {
            helperText.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            helperIcon.setImageResource(iconRes);
            helperIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
        }
    }
}
