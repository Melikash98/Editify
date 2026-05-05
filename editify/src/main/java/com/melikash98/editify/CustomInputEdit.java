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
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

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
    private Typeface inputTypeface;
    private Typeface hintTypeface;
    private Typeface helperTypeface;
    private int originalInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;

    // ==================== State ====================

    private boolean isFocus = false;
    private boolean isActive = false;
    private boolean isRightDirection = false;
    private boolean isPasswordVisible = false;
    private boolean isPasswordField = false;

    // ==================== Dropdown ====================
    private boolean isDropdownMode = false;
    private boolean isDropdownOpen = false;
    private List<DropdownItem> dropdownItems = new ArrayList<>();
    private DropdownItem selectedDropdownItem = null;
    private Drawable dropdownArrowIcon;
    private androidx.cardview.widget.CardView dropdownContainer;
    private ScrollView dropdownScrollView;
    private LinearLayout dropdownList;
    private ImageView iconDropdown;

    private int dropdownBgColor;
    private int dropdownItemTextColor;
    private float dropdownItemTextSize;
    private Typeface dropdownItemTypeface;
    private Drawable dropdownItemDefaultIcon;
    private int dropdownSelectedColor;
    private int dropdownDividerColor;
    private int dropdownItemHeight;
    private int dropdownMaxHeight;

    private OnDropdownItemSelectedListener dropdownItemSelectedListener;

    public interface OnDropdownItemSelectedListener {
        void onItemSelected(DropdownItem item, int position);
    }

    // ==================== Button Mode ====================
    private boolean isButtonMode = false;
    private OnClickListener buttonClickListener;

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
        dropdownContainer = findViewById(R.id.dropdownContainer);
        dropdownScrollView = findViewById(R.id.dropdownScrollView);
        dropdownList = findViewById(R.id.dropdownList);
        iconDropdown = findViewById(R.id.iconDropdown);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField);

        // ==================== DOM ====================
        isRightDirection = array.getBoolean(R.styleable.CustomInputField_rightDirection, false);
        activeBackground = array.getDrawable(R.styleable.CustomInputField_activeBackground);
        inactiveBackground = array.getDrawable(R.styleable.CustomInputField_inactiveBackground);
        if (activeBackground == null)
            activeBackground = context.getDrawable(R.drawable.input_active);
        if (inactiveBackground == null)
            inactiveBackground = context.getDrawable(R.drawable.input_inactive);

        // ==================== Button ====================
        isButtonMode = array.getBoolean(R.styleable.CustomInputField_buttonMode, false);

        // ==================== Hint ====================
        hintTextView.setText(array.getString(R.styleable.CustomInputField_hintText));
        if (array.getDrawable(R.styleable.CustomInputField_hintIcon) != null) {
            hintIcon.setImageDrawable(array.getDrawable(R.styleable.CustomInputField_hintIcon));
        }
        hintTypeface = resolveFontFromAttrs(context, attrs, R.styleable.CustomInputField_hintFamily);
        if (hintTypeface != null) {
            hintTextView.setTypeface(hintTypeface);
        }
        float hintSize = array.getDimension(R.styleable.CustomInputField_hintSize, 0);
        if (hintSize > 0) {
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize);
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
        inputTypeface = resolveFontFromAttrs(context, attrs, R.styleable.CustomInputField_inputFamily);
        if (inputTypeface != null) {
            editInput.setTypeface(inputTypeface);
        }
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
        // ==================== Multiline ====================
        int maxLines = array.getInt(R.styleable.CustomInputField_inputMaxLines, -1);
        int minLines = array.getInt(R.styleable.CustomInputField_inputMinLines, -1);
        boolean singleLine = array.getBoolean(R.styleable.CustomInputField_inputSingleLine, false);

        TypedArray androidAttrs = context.obtainStyledAttributes(
                attrs,
                new int[]{
                        android.R.attr.maxLines,
                        android.R.attr.minLines,
                        android.R.attr.singleLine,
                        android.R.attr.inputType
                }
        );

        int androidMaxLines = androidAttrs.getInt(0, -1);
        int androidMinLines = androidAttrs.getInt(1, -1);
        boolean androidSingleLine = androidAttrs.getBoolean(2, false);
        int androidInputType = androidAttrs.getInt(
                3,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL
        );
        androidAttrs.recycle();

        if (maxLines == -1) maxLines = androidMaxLines;
        if (minLines == -1) minLines = androidMinLines;
        if (singleLine == false && androidSingleLine) singleLine = true;

        if (maxLines == -1) maxLines = 5;
        if (minLines == -1) minLines = 1;

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

        // ==================== Dropdown attrs ====================
        isDropdownMode = array.getBoolean(R.styleable.CustomInputField_dropdownMode, false);
        dropdownBgColor = array.getColor(R.styleable.CustomInputField_dropdownBackground, Color.WHITE);
        dropdownItemTextColor = array.getColor(R.styleable.CustomInputField_dropdownItemTextColor, Color.BLACK);
        dropdownItemTextSize = array.getDimension(R.styleable.CustomInputField_dropdownItemTextSize, sp(16));
        dropdownSelectedColor = array.getColor(R.styleable.CustomInputField_dropdownSelectedColor,
                getResources().getColor(R.color.green));
        dropdownDividerColor = array.getColor(R.styleable.CustomInputField_dropdownDividerColor,
                getResources().getColor(R.color.gray));
        dropdownItemHeight = (int) array.getDimension(R.styleable.CustomInputField_dropdownItemHeight, dp(52));
        dropdownMaxHeight = (int) array.getDimension(R.styleable.CustomInputField_dropdownMaxHeight, dp(220));
        if (array.getDrawable(R.styleable.CustomInputField_dropdownArrowIcon) != null) {
            dropdownArrowIcon = array.getDrawable(R.styleable.CustomInputField_dropdownArrowIcon);
        }
        dropdownItemTypeface = resolveFontFromAttrs(context, attrs, R.styleable.CustomInputField_dropdownItemFamily);
        // ==================== Password ====================
        passShowDrawable = array.getDrawable(R.styleable.CustomInputField_passShow);
        passHideDrawable = array.getDrawable(R.styleable.CustomInputField_passHide);
        passIconColor = array.getColor(R.styleable.CustomInputField_passIconColor, Color.GRAY);

        int inputType = array.getInt(R.styleable.CustomInputField_inputType, -1);

        array.recycle();

        if (inputType == -1) {
            TypedArray androidArray = context.obtainStyledAttributes(
                    attrs, new int[]{android.R.attr.inputType});
            inputType = androidArray.getInt(0,
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            androidArray.recycle();
        }
        originalInputType = inputType;
        editInput.setInputType(inputType);
        if (inputTypeface != null) {
            editInput.setTypeface(inputTypeface);
        }
        applyMultilineConfig(singleLine, minLines, maxLines);
        setupPasswordToggle();
        setupDropdown();
        setupButtonMode();

        setupDirectionConstraints();

        editInput.setOnFocusChangeListener((v, hasFocus) -> {
            isFocus = hasFocus;
            updateUIState();
            if (outerFocusChangeListener != null)
                outerFocusChangeListener.onFocusChange(this, hasFocus);
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


        hintLayout.bringToFront();
        iconPass.bringToFront();
        post(this::updateUIState);
    }

    /**
     * Applies custom font to a TextView.
     * Supports both @font/ resource and font family name string.
     * Falls back to layout default if no font is provided.
     */

    private Typeface resolveFontFromAttrs(Context context, @Nullable AttributeSet attrs, int styleableAttrIndex) {
        if (attrs == null) return null;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputField); // ✅ درست
        try {
            int fontResId = a.getResourceId(styleableAttrIndex, 0);
            if (fontResId != 0) {
                try {
                    Typeface tf = ResourcesCompat.getFont(context, fontResId);
                    if (tf != null) return tf;
                } catch (Exception ignored) {
                }
            }

            String familyName = a.getString(styleableAttrIndex);
            if (!TextUtils.isEmpty(familyName)) {
                return Typeface.create(familyName, Typeface.NORMAL);
            }
        } finally {
            a.recycle();
        }
        return null;
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
        if (passShowDrawable == null || passHideDrawable == null) {
            iconPass.setVisibility(View.GONE);
            isPasswordField = false;
            return;
        }

        int currentType = editInput.getInputType();
        int typeClass = currentType & InputType.TYPE_MASK_CLASS;
        int variation = currentType & InputType.TYPE_MASK_VARIATION;

        /*
         * 129 = TEXT(1) | PASSWORD(128)  → class=1, variation=128 ✓
         * 291 = TEXT(1) | PASSWORD(128) | FLAG(162) → class=1, variation=128 ✓
         *  18 = NUMBER(2) | PASSWORD(16) → class=2, variation=16  ✓
         *   1 = TEXT(1) | NORMAL(0)      → class=1, variation=0   → false ✓
         */
        isPasswordField =
                (typeClass == InputType.TYPE_CLASS_TEXT &&
                        (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD))
                        ||
                        (typeClass == InputType.TYPE_CLASS_NUMBER &&
                                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        if (isPasswordField) {
            isPasswordVisible = false;
            enforcePasswordInputType(false);

            iconPass.setVisibility(View.VISIBLE);
            iconPass.setImageDrawable(passHideDrawable); // آیکون چشم بسته
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
        enforcePasswordInputType(isPasswordVisible);

        if (inputTypeface != null) {
            editInput.setTypeface(inputTypeface);
        }

        iconPass.setImageDrawable(isPasswordVisible ? passShowDrawable : passHideDrawable);
        iconPass.setColorFilter(passIconColor, PorterDuff.Mode.SRC_IN);

        safeSetSelectionToEnd();
    }

    private void enforcePasswordInputType(boolean visible) {
        int currentType = editInput.getInputType();
        int typeClass = currentType & InputType.TYPE_MASK_CLASS;
        int extraFlags = currentType & ~InputType.TYPE_MASK_CLASS & ~InputType.TYPE_MASK_VARIATION;

        if (typeClass == InputType.TYPE_CLASS_NUMBER) {
            editInput.setInputType(visible
                    ? InputType.TYPE_CLASS_NUMBER
                    : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        } else {
            editInput.setInputType(visible
                    ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | extraFlags
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | extraFlags);
        }
    }

    private void safeSetSelectionToEnd() {
        post(() -> {
            try {
                if (editInput != null && editInput.getText() != null) {
                    int len = editInput.getText().length();
                    if (len >= 0) {
                        editInput.setSelection(len);
                    }
                }
            } catch (Exception ignored) {
            }
        });
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
        isActive = !TextUtils.isEmpty(text);
        updateUIState();
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

    private OnFocusChangeListener outerFocusChangeListener;

    public AppCompatEditText getEditText() {
        return editInput;
    }

    public String getRawText() {
        return editInput.getText() != null ? editInput.getText().toString() : "";
    }

    public void setText(CharSequence text) {
        editInput.setText(text);
        isActive = !TextUtils.isEmpty(text);
        updateUIState();
    }

    public void append(CharSequence text) {
        editInput.append(text);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        editInput.addTextChangedListener(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        editInput.removeTextChangedListener(watcher);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        outerFocusChangeListener = l;
    }

    public void requestInputFocus() {
        editInput.requestFocus();
    }

    public void clearInputFocus() {
        editInput.clearFocus();
    }

    public boolean isInputFocused() {
        return editInput.isFocused();
    }

    public void showKeyboard() {
        editInput.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(editInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(editInput.getWindowToken(), 0);
    }

    public void setImeOptions(int imeOptions) {
        editInput.setImeOptions(imeOptions);
    }

    public int getImeOptions() {
        return editInput.getImeOptions();
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        editInput.setOnEditorActionListener(listener);
    }

    public void setInputType(int type) {
        originalInputType = type;
        editInput.setInputType(type);
        if (inputTypeface != null) editInput.setTypeface(inputTypeface);
        setupPasswordToggle();
    }

    public int getInputType() {
        return editInput.getInputType();
    }

    public void setFilters(InputFilter[] filters) {
        editInput.setFilters(filters);
    }

    public InputFilter[] getFilters() {
        return editInput.getFilters();
    }

    public void setSelection(int index) {
        editInput.setSelection(index);
    }

    public void setSelection(int start, int stop) {
        editInput.setSelection(start, stop);
    }

    public void selectAll() {
        editInput.selectAll();
    }

    public int getSelectionStart() {
        return editInput.getSelectionStart();
    }

    public int getSelectionEnd() {
        return editInput.getSelectionEnd();
    }

    public void setSelectionToEnd() {
        safeSetSelectionToEnd();
    }

    public void setHintText(String hint) {
        if (hintTextView != null) hintTextView.setText(hint);
    }

    public CharSequence getHintText() {
        return hintTextView != null ? hintTextView.getText() : "";
    }

    public void setTextColor(@ColorInt int color) {
        editInput.setTextColor(color);
    }

    public void setTextSize(float sizeInSp) {
        editInput.setTextSize(sizeInSp);
    }

    public void setTextSize(int unit, float size) {
        editInput.setTextSize(unit, size);
    }

    public void setTypeface(Typeface tf) {
        inputTypeface = tf;
        editInput.setTypeface(tf);
    }

    public void setTypeface(Typeface tf, int style) {
        inputTypeface = Typeface.create(tf, style);
        editInput.setTypeface(tf, style);
    }

    public Typeface getTypeface() {
        return editInput.getTypeface();
    }

    public void setMaxLines(int maxLines) {
        editInput.setMaxLines(maxLines);
    }

    public void setSingleLine(boolean singleLine) {
        editInput.setSingleLine(singleLine);
    }

    public void setMaxLength(int maxLength) {
        editInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public void setLines(int lines) {
        editInput.setLines(lines);
    }

    public void setMinLines(int minLines) {
        editInput.setMinLines(minLines);
    }

    public void setInputEnabled(boolean enabled) {
        editInput.setEnabled(enabled);
    }

    public boolean isInputEnabled() {
        return editInput.isEnabled();
    }

    public void setKeyListener(android.text.method.KeyListener input) {
        editInput.setKeyListener(input);
    }

    public android.text.method.KeyListener getKeyListener() {
        return editInput.getKeyListener();
    }

    private void applyMultilineConfig(boolean singleLine, int minLines, int maxLines) {
        if (singleLine) {
            editInput.setSingleLine(true);
            editInput.setHorizontallyScrolling(true);
        } else {
            editInput.setSingleLine(false);
            editInput.setHorizontallyScrolling(false);
            editInput.setMinLines(minLines);
            editInput.setMaxLines(maxLines);

            int currentType = editInput.getInputType();
            int variation = currentType & InputType.TYPE_MASK_VARIATION;

            boolean isSpecialVariation =
                    variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                            variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                            variation == InputType.TYPE_TEXT_VARIATION_URI ||
                            variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
                            variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;

            int imeOptions = editInput.getImeOptions() & EditorInfo.IME_MASK_ACTION;
            boolean hasActionKey =
                    imeOptions == EditorInfo.IME_ACTION_SEARCH ||
                            imeOptions == EditorInfo.IME_ACTION_DONE ||
                            imeOptions == EditorInfo.IME_ACTION_GO ||
                            imeOptions == EditorInfo.IME_ACTION_SEND ||
                            imeOptions == EditorInfo.IME_ACTION_NEXT;

            if (!isSpecialVariation && !hasActionKey &&
                    (currentType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) {
                editInput.setInputType(currentType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
        }
    }

    private void setupDropdown() {
        if (!isDropdownMode) {
            iconDropdown.setVisibility(View.GONE);
            return;
        }
        editInput.setFocusable(false);
        editInput.setFocusableInTouchMode(false);
        editInput.setCursorVisible(false);
        editInput.setLongClickable(false);
        iconDropdown.setVisibility(View.VISIBLE);
        if (dropdownArrowIcon != null) {
            iconDropdown.setImageDrawable(dropdownArrowIcon);
        } else {
            iconDropdown.setImageDrawable(
                    getContext().getDrawable(android.R.drawable.arrow_down_float)
            );
        }
        iconDropdown.setColorFilter(hintDefaultColor, PorterDuff.Mode.SRC_IN);
        if (dropdownContainer != null) {
            dropdownContainer.setCardBackgroundColor(dropdownBgColor);
        }
        dropdownContainer.setElevation(dp(16));

        editInput.setOnClickListener(v -> toggleDropdown());
        iconDropdown.setOnClickListener(v -> toggleDropdown());
    }

    private void toggleDropdown() {
        if (isDropdownOpen) {
            closeDropdown();
        } else {
            openDropdown();
        }
    }

    private void openDropdown() {
        if (dropdownItems.isEmpty()) return;

        isDropdownOpen = true;
        isFocus = true;
        updateUIState();
        ViewGroup rootView = (ViewGroup) getRootView();
        if (dropdownContainer.getParent() != rootView) {
            int[] location = new int[2];
            containerLayout.getLocationOnScreen(location);
        }
        dropdownContainer.setElevation(dp(16));
        dropdownContainer.bringToFront();
        ((View) dropdownContainer.getParent()).invalidate();
        iconDropdown.animate()
                .rotation(180f)
                .setDuration(220)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        rebuildDropdownItems();
        dropdownContainer.post(() -> {
            int itemCount = dropdownItems.size();
            int exactH = itemCount * dropdownItemHeight;
            int finalH = Math.min(exactH, dropdownMaxHeight);
            dropdownScrollView.getLayoutParams().height = finalH;
            dropdownContainer.getLayoutParams().height = finalH;
            dropdownScrollView.requestLayout();
            dropdownContainer.requestLayout();
        });
        dropdownContainer.setVisibility(View.VISIBLE);
        dropdownContainer.setAlpha(0f);
        dropdownContainer.setScaleY(0.85f);
        dropdownContainer.setPivotY(0f);
        dropdownContainer.bringToFront();
        dropdownContainer.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        dropdownContainer.bringToFront();
    }

    private void closeDropdown() {
        isDropdownOpen = false;
        if (selectedDropdownItem == null) {
            isFocus = false;
        }
        updateUIState();
        iconDropdown.animate()
                .rotation(0f)
                .setDuration(220)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        dropdownContainer.animate()
                .alpha(0f)
                .scaleY(0.85f)
                .setDuration(180)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    dropdownContainer.setVisibility(View.GONE);
                    dropdownContainer.setScaleY(1f);
                    dropdownContainer.setAlpha(1f);
                })
                .start();
    }

    private void rebuildDropdownItems() {
        dropdownList.removeAllViews();
        Context ctx = getContext();

        for (int i = 0; i < dropdownItems.size(); i++) {
            final DropdownItem item = dropdownItems.get(i);
            final int position = i;
            boolean isSelected = selectedDropdownItem != null
                    && selectedDropdownItem.value.equals(item.value);

            View itemView = LayoutInflater.from(ctx)
                    .inflate(R.layout.dropdown_item, dropdownList, false);

            TextView textView = itemView.findViewById(R.id.dropdownItemText);
            ImageView iconView = itemView.findViewById(R.id.dropdownItemIcon);
            View divider = itemView.findViewById(R.id.dropdownDivider);

            textView.setText(item.label);
            textView.setTextColor(isSelected ? dropdownSelectedColor : dropdownItemTextColor);
            if (dropdownItemTextSize > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dropdownItemTextSize);
            }
            if (dropdownItemTypeface != null) {
                textView.setTypeface(dropdownItemTypeface);
            }

            Drawable iconToShow = item.icon != null ? item.icon : dropdownItemDefaultIcon;
            ConstraintLayout itemRoot = (ConstraintLayout) itemView;
            ConstraintSet itemSet = new ConstraintSet();
            itemSet.clone(itemRoot);

            if (iconToShow != null) {
                iconView.setImageDrawable(iconToShow);
                iconView.setColorFilter(
                        isSelected ? dropdownSelectedColor : dropdownItemTextColor,
                        PorterDuff.Mode.SRC_IN
                );
                iconView.setVisibility(View.VISIBLE);

                itemSet.clear(R.id.dropdownItemText, ConstraintSet.START);
                itemSet.connect(R.id.dropdownItemText, ConstraintSet.START,
                        R.id.dropdownItemIcon, ConstraintSet.END, (int) dp(12));
            } else {
                iconView.setVisibility(View.GONE);

                itemSet.clear(R.id.dropdownItemText, ConstraintSet.START);
                itemSet.connect(R.id.dropdownItemText, ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START, (int) dp(16));
            }
            itemSet.applyTo(itemRoot);

            divider.setBackgroundColor(dropdownDividerColor);
            divider.setVisibility(i < dropdownItems.size() - 1 ? View.VISIBLE : View.GONE);

            itemView.setMinimumHeight(dropdownItemHeight);

            if (isSelected) {
                itemView.setBackgroundColor(
                        adjustAlpha(dropdownSelectedColor, 0.08f)
                );
            }
            itemView.setOnClickListener(v -> {
                selectDropdownItem(item, position);
            });
            if (isRightDirection) {
                textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                textView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            }

            dropdownList.addView(itemView);
        }
    }

    private void selectDropdownItem(DropdownItem item, int position) {
        selectedDropdownItem = item;

        editInput.setText(item.label);

        isActive = true;
        updateUIState();

        closeDropdown();
        if (dropdownItemSelectedListener != null) {
            dropdownItemSelectedListener.onItemSelected(item, position);
        }
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private float sp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    // ==================== Dropdown Public API ====================
    public void setDropdownItems(List<DropdownItem> items) {
        dropdownItems.clear();
        if (items != null) dropdownItems.addAll(items);
        if (isDropdownOpen) rebuildDropdownItems();
    }

    public void addDropdownItem(String label) {
        dropdownItems.add(new DropdownItem(label));
        if (isDropdownOpen) rebuildDropdownItems();
    }

    public void addDropdownItem(String label, String value) {
        dropdownItems.add(new DropdownItem(label, value));
        if (isDropdownOpen) rebuildDropdownItems();
    }

    public void addDropdownItem(String label, String value, Drawable icon) {
        dropdownItems.add(new DropdownItem(label, value, icon));
        if (isDropdownOpen) rebuildDropdownItems();
    }

    public void addDropdownItem(String label, String value, int iconResId) {
        Drawable icon = null;
        try {
            icon = androidx.core.content.ContextCompat.getDrawable(getContext(), iconResId);
        } catch (Exception ignored) {
        }
        dropdownItems.add(new DropdownItem(label, value, icon));
        if (isDropdownOpen) rebuildDropdownItems();
    }

    public void clearDropdownItems() {
        dropdownItems.clear();
        selectedDropdownItem = null;
        editInput.setText("");
        isActive = false;
        updateUIState();
        if (isDropdownOpen) closeDropdown();
    }

    public DropdownItem getSelectedDropdownItem() {
        return selectedDropdownItem;
    }

    public String getSelectedValue() {
        return selectedDropdownItem != null ? selectedDropdownItem.value : "";
    }

    public void setSelectedValue(String value) {
        for (int i = 0; i < dropdownItems.size(); i++) {
            if (dropdownItems.get(i).value.equals(value)) {
                selectDropdownItem(dropdownItems.get(i), i);
                return;
            }
        }
    }

    public void setOnDropdownItemSelectedListener(OnDropdownItemSelectedListener listener) {
        this.dropdownItemSelectedListener = listener;
    }

    public void openDropdownMenu() {
        if (isDropdownMode && !isDropdownOpen) openDropdown();
    }

    public void closeDropdownMenu() {
        if (isDropdownMode && isDropdownOpen) closeDropdown();
    }

    public boolean isDropdownOpen() {
        return isDropdownOpen;
    }

    private void setupButtonMode() {
        if (!isButtonMode) return;
        if (isDropdownMode) return;

        editInput.setFocusable(false);
        editInput.setFocusableInTouchMode(false);
        editInput.setCursorVisible(false);
        editInput.setLongClickable(false);
        editInput.setInputType(InputType.TYPE_NULL);

        View.OnClickListener internalClick = v -> {
            if (buttonClickListener != null) {
                buttonClickListener.onClick(CustomInputEdit.this);
            }
        };
        editInput.setOnClickListener(internalClick);
        super.setOnClickListener(internalClick);
    }

    public void setOnClickListener(@Nullable OnClickListener listener) {
        if (isDropdownMode) {
            return;
        }
        if (isButtonMode) {
            buttonClickListener = listener;
        } else {
            super.setOnClickListener(listener);
        }
    }
}
