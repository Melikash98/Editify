package com.melikash98.editify;

import android.graphics.drawable.Drawable;


/**
 * Represents a single selectable item used by the dropdown component.
 *
 * This model keeps the data structure intentionally simple and flexible:
 * a visible label for display, an optional value for internal logic or persistence,
 * and an optional icon for richer presentation inside the dropdown list.
 */

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
