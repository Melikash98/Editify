# Editify

[![JitPack](https://jitpack.io/v/Melikash98/Editify.svg)](https://jitpack.io/#Melikash98/Editify)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)


**A beautiful, fully customizable, animated Android input field library with floating hint, password toggle, helper/warning/error messages, RTL support, and smooth animations.**

Editify provides a modern `CustomInputEdit` component that replaces the default `EditText` with advanced UX features commonly seen in premium apps.

---

## ✨ Features

- **Floating hint animation** with scale and color transition
- **Password visibility toggle** (show/hide with custom icons)
- **Helper, Warning & Error states** with optional icons and colors
- **Full RTL support** (`rightDirection` attribute)
- **Highly customizable** colors, fonts, sizes, backgrounds, and icons
- **Smooth animations** using Android's `ObjectAnimator` and `ValueAnimator`
- **Works with both Kotlin and Java** projects
- **Zero dependencies** – pure AndroidX + ConstraintLayout
- **JitPack ready** for instant integration

---

## 📺 Demo

Upload your demo video/GIF to the repository root (recommended name: `demo.gif` or `demo.mp4`).

![Editify Usage Demo](demo.gif)

> *Tip: Record a short 10–15 second GIF showing the floating hint animation, password toggle, error/warning/helper states in both LTR and RTL directions.*

---

## 📦 Installation

### 1. Add JitPack repository

In your **root** `settings.gradle` (or `settings.gradle.kts`):

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
### Step 2: Add dependency

```gradle
dependencies {
      implementation 'com.github.Melikash98:Editify:v1.4.5'
}
```
## 🛠️ Usage

### XML
```xml
<com.melikash98.editify.CustomInputEdit
    android:id="@+id/myCustomInput"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"

    <!-- Hint configuration -->
    app:hintText="Username"                <!-- Main hint text -->
    app:hintIcon="@drawable/ic_user"       <!-- Icon inside hint -->
    app:hintColor="@color/gray"            <!-- Default hint color -->
    app:hintActiveColor="@color/green"     <!-- Color when focused -->
    app:hintBackgroundColor="@color/white" <!-- Background of hint -->

    <!-- Input text styling -->
    app:inputColor="@color/black"          <!-- Text color inside input -->
    app:textColor="@color/black"           <!-- Fallback text color -->

    <!-- Background states -->
    app:activeBackground="@drawable/input_active"     <!-- When focused -->
    app:inactiveBackground="@drawable/input_inactive" <!-- Default -->

    <!-- Helper / Warning / Error messages -->
    app:helperText="Enter username"        <!-- Helper message -->
    app:warningText="Check your input"     <!-- Warning message -->
    app:errorText="Field required"         <!-- Error message -->

    <!-- Password toggle icons -->
    app:passShow="@drawable/ic_show"       <!-- Icon when password visible -->
    app:passHide="@drawable/ic_hide"       <!-- Icon when password hidden -->

    <!-- Layout direction (RTL / LTR) -->
    app:rightDirection="false"/>
```


---
## 🎯 Java Usage

```java
// Get reference to the custom input view
CustomInputEdit input = findViewById(R.id.myCustomInput);

// Get current text value (trimmed)
String text = input.getText();

// Set text programmatically
input.setText("Hello");

// Show helper message (green state)
input.setHelperText("Helper message");

// Show warning message (yellow state)
input.setWarningText("Warning message");

// Show error message (red state)
input.setErrorText("Error message");
```

---
## 🎨 Attributes

| Attribute | Description |
|----------|------------|
| hintText | Hint text |
| input | Default text |
| helperText | Helper message |
| warningText | Warning message |
| errorText | Error message |
| hintIcon | Hint icon |
| passShow | Show password icon |
| passHide | Hide password icon |
| activeBackground | Active background |
| inactiveBackground | Inactive background |
| hintColor | Default hint color |
| hintActiveColor | Active hint color |
| inputColor | Input text color |
| helperColor | Helper color |
| warningColor | Warning color |
| errorColor | Error color |
| inputType | Input type |
| rightDirection | RTL support |

---

## 🔤 Input Types

```xml
app:inputType="1"     <!-- Normal -->
app:inputType="129"   <!-- Password -->
```

---

## 📱 RTL Support

```xml
app:rightDirection="true"
```

---

## 📸 Preview

Add screenshots:

```
docs/images/preview1.png
docs/images/preview2.png
```

---

## 🎬 Demo (GIF / Video)

### GIF

```md
![Demo](docs/demo/editify.gif)
```

### Video

```
https://your-video-link.com
```

---

## 📄 License
MIT License

Copyright 2025 Melika Sh (Melikash98)

This project is licensed under the Apache License, Version 2.0.
<p>You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0</p>

---

## 👩‍💻 Author

Melikash98


