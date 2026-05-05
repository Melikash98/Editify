package com.melikash98.editify;

import android.graphics.drawable.Drawable;

public class DropdownItem {
    public final String label;
    public final String value;
    public final Drawable icon;

    public DropdownItem(String label) {
        this(label, label, null);
    }

    public DropdownItem(String label, String value) {
        this(label, value, null);
    }

    public DropdownItem(String label, String value, Drawable icon) {
        this.label = label;
        this.value = value;
        this.icon = icon;
    }
}
