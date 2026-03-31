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
import androidx.core.content.res.ResourcesCompat;

/**
 * CustomInputEdit - A highly customizable Material-style EditText with floating hint animation.
 * <p>
 * Features:
 * - Smooth floating hint with scale animation
 * - Helper, Warning, and Error states with optional icons
 * - Built-in password visibility toggle
 * - Full RTL/LTR layout support
 * - Custom font and text size support for hint, input, and helper texts
 * - Flexible background and color customization via XML attributes
 *
 * @author Melika Sh98
 * @version 1.0
 */

public class CustomInputEdit extends ConstraintLayout {

    // ==================== Views ====================
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

    // ==================== Drawables ====================

    private Drawable activeBackground;
    private Drawable inactiveBackground;

    private Drawable passShowDrawable;
    private Drawable passHideDrawable;

    // ==================== Colors ====================

    private int hintDefaultColor;
    private int hintActiveColor;
    private int passIconColor;
    private int helperColor;
    private int warningColor;
    private int errorColor;
    private ConstraintLayout helperBack;
    private ConstraintLayout wrongBack;
    private ConstraintLayout errorBack;

    // ==================== Text Styling ====================

    private float helperTextSize;
    private String helperTextFamily;
    private int helperFontResId;
    private int helperTextStyle;

    // ==================== State ====================

    private boolean isFocus = false;
    private boolean isActive = false;
    private boolean isRightDirection = false;
    private boolean isPasswordVisible = false;


    // ==================== Constructors ====================

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

    /**
     * Main initialization method.
     * Inflates the layout and reads all custom attributes from XML.
     */

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_input_field, this, true);

        // Bind views

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
        helperBack = findViewById(R.id.helperBack);
        wrongBack = findViewById(R.id.wrongBack);
        errorBack = findViewById(R.id.errorBack);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

        // ==================== DOM ====================
        isRightDirection = array.getBoolean(R.styleable.CustomInputField_rightDirection, false);
        activeBackground = array.getDrawable(R.styleable.CustomInputField_activeBackground);
        inactiveBackground = array.getDrawable(R.styleable.CustomInputField_inactiveBackground);
        if (activeBackground == null)
            activeBackground = context.getDrawable(R.drawable.input_active);
        if (inactiveBackground == null)
            inactiveBackground = context.getDrawable(R.drawable.input_inactive);

        // ==================== Hint ====================
        hintTextView.setText(array.getString(R.styleable.CustomInputField_hintText));
        if (array.getDrawable(R.styleable.CustomInputField_hintIcon) != null) {
            hintIcon.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_hintIcon));
        }
        applyFontToView(context, hintTextView, R.styleable.CustomInputField_hintFamily);
        float hintSize = array.getDimension(R.styleable.CustomInputField_hintSize, 0);
        if (hintSize > 0) {
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize);
        }
        String hintFamily = array.getString(R.styleable.CustomInputField_hintFamily);
        if (!TextUtils.isEmpty(hintFamily)) {
            Typeface hintTypeface = Typeface.create(hintFamily, Typeface.NORMAL);
            hintTextView.setTypeface(hintTypeface);
        }
        hintDefaultColor = array.getColor(
                R.styleable.CustomInputField_hintColor,
                hintTextView.getCurrentTextColor()
        );
        hintActiveColor = array.getColor(
                R.styleable.CustomInputField_hintActiveColor,
                hintDefaultColor
        );

        int hintBgColor = array.getColor(R.styleable.CustomInputField_hintBackgroundColor, Color.WHITE);
        Drawable hintBackground = context.getDrawable(R.drawable.hint_bg);
        if (hintBackground instanceof GradientDrawable) {
            ((GradientDrawable) hintBackground.mutate()).setColor(hintBgColor);
        } else {
            hintBackground.setTint(hintBgColor);
        }
        hintLayout.setBackground(hintBackground);

        // ==================== Inputs ====================
        applyFontToView(context, editInput, R.styleable.CustomInputField_inputFamily);
        float inputSize = array.getDimension(R.styleable.CustomInputField_inputSize, 0);
        if (inputSize > 0) {
            editInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputSize);
        }
        String inputText = array.getString(R.styleable.CustomInputField_input);
        if (!TextUtils.isEmpty(inputText)) {
            editInput.setText(inputText);
        }
        int editTextColor = array.getColor(R.styleable.CustomInputField_inputColor,
                array.getColor(R.styleable.CustomInputField_textColor, Color.BLACK));
        editInput.setTextColor(editTextColor);

        // ==================== Helpers ====================
        helperColor = array.getColor(R.styleable.CustomInputField_helperColor, getResources().getColor(R.color.green));
        warningColor = array.getColor(R.styleable.CustomInputField_warningColor, getResources().getColor(R.color.yellow));
        errorColor = array.getColor(R.styleable.CustomInputField_errorColor, getResources().getColor(R.color.red));
        float helperSize = array.getDimension(R.styleable.CustomInputField_helperSize, 0);
        helperTextSize = helperSize;
        String hText = array.getString(R.styleable.CustomInputField_helperText);
        String wText = array.getString(R.styleable.CustomInputField_warningText);
        String eText = array.getString(R.styleable.CustomInputField_errorText);

        if (!TextUtils.isEmpty(hText)) {
            helperTextView.setText(hText);
            helperBack.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(wText)) {
            warningTextView.setText(wText);
            wrongBack.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(eText)) {
            errorTextView.setText(eText);
            errorBack.setVisibility(View.VISIBLE);
        }
        if (array.getDrawable(R.styleable.CustomInputField_helperIcon) != null) {
            helperIconView.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_helperIcon));
        }
        if (array.getDrawable(R.styleable.CustomInputField_warningIcon) != null) {
            warningIconView.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_warningIcon));
        }
        if (array.getDrawable(R.styleable.CustomInputField_errorIcon) != null) {
            errorIconView.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_errorIcon));
        }
        if (helperTextSize > 0) {
            helperTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperTextSize);
            warningTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperTextSize);
            errorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperTextSize);
        }

        Typeface helperTypeface = getHelperTypeface(context);
        helperTextView.setTypeface(helperTypeface);
        warningTextView.setTypeface(helperTypeface);
        errorTextView.setTypeface(helperTypeface);

        applyHelperColors();

        // ==================== Password ====================
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

    /**
     * Applies custom font to a TextView.
     * Supports both @font/ resource and font family name string.
     * Falls back to layout default if no font is provided.
     */

    private void applyFontToView(Context context, TextView view, int attrIndex) {
        TypedArray temp = context.obtainStyledAttributes(new int[]{attrIndex}); // فقط برای گرفتن resource
        int fontResId = temp.getResourceId(0, 0);
        temp.recycle();

        if (fontResId != 0) {
            Typeface tf = ResourcesCompat.getFont(context, fontResId);
            if (tf != null) view.setTypeface(tf);
            return;
        }

        // fallback به family name
        String family = context.obtainStyledAttributes(new int[]{attrIndex})
                .getString(0);
        if (!TextUtils.isEmpty(family)) {
            view.setTypeface(Typeface.create(family, Typeface.NORMAL));
        }
    }

    /**
     * Returns the typeface for helper, warning, and error texts.
     */

    private Typeface getHelperTypeface(Context context) {
        if (helperFontResId != 0) {
            Typeface tf = ResourcesCompat.getFont(context, helperFontResId);
            if (tf != null) return tf;
        }
        if (!TextUtils.isEmpty(helperTextFamily)) {
            return Typeface.create(helperTextFamily, helperTextStyle);
        }
        return Typeface.create(Typeface.DEFAULT, helperTextStyle);
    }

    /**
     * Applies correct colors to helper, warning, and error texts and their icons.
     */

    private void applyHelperColors() {
        helperTextView.setTextColor(helperColor);
        if (helperIconView.getDrawable() != null)
            helperIconView.setColorFilter(helperColor, PorterDuff.Mode.SRC_IN);

        warningTextView.setTextColor(warningColor);
        if (warningIconView.getDrawable() != null)
            warningIconView.setColorFilter(warningColor, PorterDuff.Mode.SRC_IN);

        errorTextView.setTextColor(errorColor);
        if (errorIconView.getDrawable() != null)
            errorIconView.setColorFilter(errorColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets up password visibility toggle if the input type is a password field.
     */

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

    /**
     * Toggles between visible and hidden password and updates the icon accordingly.
     */

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

    /**
     * Configures layout constraints and gravity based on RTL or LTR direction.
     */

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
        setupHelperDirection(helperBack, R.id.helperIcon, R.id.helperText);
        setupHelperDirection(wrongBack, R.id.warningIcon, R.id.warningText);
        setupHelperDirection(errorBack, R.id.errorIcon, R.id.errorText);
    }

    private void setupHelperDirection(ConstraintLayout parent, int iconId, int textId) {
        if (parent == null) return;
        ConstraintSet set = new ConstraintSet();
        set.clone(parent);

        set.clear(iconId, ConstraintSet.START);
        set.clear(iconId, ConstraintSet.END);
        set.clear(textId, ConstraintSet.START);
        set.clear(textId, ConstraintSet.END);

        if (isRightDirection) {
            set.connect(iconId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            set.connect(textId, ConstraintSet.END, iconId, ConstraintSet.START, (int) dp(15));
        } else {
            set.connect(iconId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            set.connect(textId, ConstraintSet.START, iconId, ConstraintSet.END, (int) dp(15));
        }

        set.connect(textId, ConstraintSet.TOP, iconId, ConstraintSet.TOP, 0);
        set.connect(textId, ConstraintSet.BOTTOM, iconId, ConstraintSet.BOTTOM, 0);
        set.connect(iconId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(iconId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        set.applyTo(parent);
    }

    /**
     * Updates the overall UI state based on focus and text input.
     */

    private void updateUIState() {
        boolean shouldActivate = isFocus || isActive;
        editInput.setBackground(shouldActivate ? activeBackground : inactiveBackground);
        updateHintAppearance(shouldActivate);
        updateHintPosition();
    }

    /**
     * Updates hint text and icon color based on active state.
     */

    private void updateHintAppearance(boolean shouldActivate) {
        int targetColor = shouldActivate ? hintActiveColor : hintDefaultColor;

        hintTextView.setTextColor(targetColor);
        if (hintIcon.getDrawable() != null) {
            hintIcon.setColorFilter(targetColor, PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * Animates the hint label position and scale (floating effect).
     */

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

    // ==================== Public API Methods ====================

    /**
     * Returns the trimmed text from the input field.
     */

    public String getText() {
        return editInput.getText().toString().trim();
    }

    /**
     * Sets text to the input field.
     */

    public void setText(String text) {
        editInput.setText(text);
    }

    /**
     * Sets helper text and shows/hides the helper section.
     */

    public void setHelperText(String text) {
        if (helperTextView != null) {
            helperTextView.setText(text != null ? text : "");
            helperBack.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Sets warning text and shows/hides the warning section.
     */

    public void setWarningText(String text) {
        if (warningTextView != null) {
            warningTextView.setText(text != null ? text : "");
            wrongBack.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Sets error text and shows/hides the error section.
     */

    public void setErrorText(String text) {
        if (errorTextView != null) {
            errorTextView.setText(text != null ? text : "");
            errorBack.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }
}
