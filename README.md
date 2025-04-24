<div align="center">
    <img height="80" width="80" src="https://github.com/user-attachments/assets/1886b5e1-9fd5-4949-8d7d-1709338a898f" alt="ThMLT Logo" />
    <h1>🧩 ThMLT</h1>
</div>





## 📝 Specifications


![Package](https://img.shields.io/badge/📦%20Package-com.hridoy.thmlt-blue?style=flat-square)

![Size](https://img.shields.io/badge/💾%20Size-20.17%20KB-green?style=flat-square)

![Version](https://img.shields.io/badge/⚙️%20Version-3.0.0-orange?style=flat-square)

![Min API](https://img.shields.io/badge/📱%20Min%20API-7-blueviolet?style=flat-square)

![Updated](https://img.shields.io/badge/📅%20Updated-2025--04--24-lightgrey?style=flat-square)

![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)

![Built with FAST](https://img.shields.io/badge/💻%20Built%20with-FAST%20v2.8.4-yellow?style=flat-square&logo=data)



ThMLT (Theme & Multilanguage Translation) is an advanced extension for [App Inventor 2 (AI2)](http://ai2.appinventor.mit.edu) that streamlines theme management (colors & fonts) and enables seamless multilanguage support across your AI2 project.

---

## 📋 Table of Contents

1. [Key Features](https://github.com/HB-Hridoy/th-m-l-t/wiki)
2. [Guidelines](https://github.com/HB-Hridoy/th-m-l-t/wiki/Guidelines)
    - [Color Themes JSON](https://github.com/HB-Hridoy/th-m-l-t/wiki/Color-Themes-JSON-Schema)
    - [Fonts JSON](https://github.com/HB-Hridoy/th-m-l-t/wiki/Fonts-JSON-Schema)
    - [Translations JSON](https://github.com/HB-Hridoy/th-m-l-t/wiki/Translation-JSON-Schema)
    - [Text Formatting](https://github.com/HB-Hridoy/th-m-l-t/wiki/Text-Formatter)
3. [Blocks](#blocks)
    - [Methods]()
    - [Properties]()
    - [Events]()

# 🧩 Blocks
<details>
  <summary><kbd>Methods</kbd></summary>


### 🟪 `Initialize`

> **Purpose:** Initializes and loads data for color themes, fonts, and translations.

![Initialize](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/Initialize_Method.png?raw=true)

| Parameter     | Type |
|--------------|------|
| colorThemes  | text |
| fonts        | text |
| translations | text |


🔧 Need help configuring your **Translations**, **Fonts**, or **Colors**?

📖 Follow the official [Data Configuration Guide →](https://github.com/HB-Hridoy/th-m-l-t/wiki/Guidelines#-data-configuration-guide) to set up your JSON schemas correctly.

---

### 🟪 `ApplyFormatting`

> **Purpose:** Applies formatting to a layout using the *active* theme and language.

![ApplyFormatting](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ApplyFormatting_Method.png?raw=true)

| Parameter | Type      |
|-----------|-----------|
| layout    | component |


---

### 🟪 `ApplyCustomizedFormatting`

> **Purpose:** Applies formatting to a layout using a **specific** theme and language.

![ApplyCustomizedFormatting](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ApplyCustomizedFormatting_Method.png?raw=true)

| Parameter     | Type |
|---------------|------|
| layout        | component |
| themeMode     | text |
| languageCode  | text |



---

### 🟪 `Get`

> **Purpose:** Retrieves a list of values for a given data category.

![Get](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/Get_Method.png?raw=true)

| Parameter | Type |
|-----------|------|
| data      | All <sub>(helper enums)</sub> |

**Return:** `list`  
**Why:** Returns the complete list of items for enums like `PrimitiveKeys`, `SemanticKeys`, `ThemeModes`, `FontTags`, `FontShortTags`, `TranslationKeys`, `SupportedLanguages`.

---

### 🟪 `GetTranslation`

> **Purpose:** Returns a translation for a given key using the *active language*.

![GetTranslation](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetTranslation_Method.png?raw=true)

| Parameter      | Type |
|----------------|------|
| translationKey | text |

**Return:** `text`  
**Why:** Returns the corresponding translation string, or `Not Found` if missing.

---

### 🟪 `GetFont`

> **Purpose:** Returns a font name for a given key using the *tag*.

![GetFont](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetFont_Method.png?raw=true)

| Parameter      | Type |
|----------------|------|
| tag | text |

**Return:** `text`  
**Why:** Returns the corresponding font name, or `Font not found` if missing.

---

### 🟪 `GetTranslationForLanguage`

> **Purpose:** Returns a translation for a key in a **specific language**.

![GetTranslationForLanguage](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetTranslationForLanguage_Method.png?raw=true)

| Parameter      | Type |
|----------------|------|
| translationKey | text |
| languageCode   | text |

**Return:** `text`  
**Why:**
- Returns the translation string if found.
- Returns `Not Found` if the key is missing.
- Returns `'languageCode' is not supported` if the languageCode is invalid.

---

### 🟪 `GetPrimitiveColor`

> **Purpose:** Retrieves a **primitive color** as an integer value.

![GetPrimitiveColor](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetPrimitiveColor_Method.png?raw=true)

| Parameter | Type |
|-----------|------|
| key       | text |

**Return:** `number`  
**Why:**
- Returns the integer color value if key exists.
- Returns `-1` if not found (error condition).

---

### 🟪 `GetSemanticColorSource`

> **Purpose:** Retrieves the **source reference** of a semantic color for the *active theme*.

![GetSemanticColorSource](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetSemanticColorSource_Method.png?raw=true)

| Parameter | Type |
|-----------|------|
| key       | text |

**Return:** `text`  
**Why:**
- Returns the primitive color key.
- Returns a detailed error if the theme or key doesn't exist.

---

### 🟪 `GetSemanticColor`

> **Purpose:** Retrieves a semantic color value (int) for the *active theme mode*.

![GetSemanticColor](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetSemanticColor_Method.png?raw=true)

| Parameter | Type |
|-----------|------|
| key       | text |

**Return:** `number`  
**Why:**
- Returns resolved color as int if found.
- Returns `-1` if theme mode or color key is invalid.

---

### 🟪 `GetSemanticColorByThemeMode`

> **Purpose:** Retrieves a semantic color value for a **specific theme mode**.

![GetSemanticColorByThemeMode](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/GetSemanticColorByThemeMode_Method.png?raw=true)

| Parameter  | Type |
|------------|------|
| key        | text |
| themeMode  | text |

**Return:** `number`  
**Why:**
- Returns resolved color as int if key exists in the given mode.
- Returns `-1` for missing theme/key.



</details>


<details>
  <summary><kbd>Properties</kbd></summary>

## 🔧 <kbd>Setters</kbd>

### 🟩 `FontRegular`
> Sets the **regular font**.

![FontRegular](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontRegular_Set_Property.png?raw=true)

| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `FontBold`
> Sets the **bold font**.

![FontBold](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontBold_Set_Property.png?raw=true)

| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `FontMaterial`
> Sets the **material icon font**.

![FontMaterial](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontMaterial_Set_Property.png?raw=true)

| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `ColorPrimary`
> Sets the **primary color**.

![ColorPrimary](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorPrimary_Set_Property.png?raw=true)

| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorSecondary`
> Sets the **secondary color**.

![ColorSecondary](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorSecondary_Set_Property.png?raw=true)

| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorAccent`
> Sets the **accent color**.

![ColorAccent](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorAccent_Set_Property.png?raw=true)

| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `Language`
> Sets the **active translation language**.

![Language](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/Language_Set_Property.png?raw=true)

| Input | Type |
|-------|------|
| code  | text |

---

### 🟩 `ThemeMode`
> Sets the **active theme mode** (e.g., `light`, `dark`).

![ThemeMode](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ThemeMode_Set_Property.png?raw=true)

| Input | Type |
|-------|------|
| mode  | text |

---

## 🔍 <kbd>Getters</kbd>

### 🟩 `FontRegular`
> Gets the **regular font**.

![FontRegular](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontRegular_Get_Property.png?raw=true)

| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `FontBold`
> Gets the **bold font**.

![FontBold](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontBold_Get_Property.png?raw=true)

| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `FontMaterial`
> Gets the **material font**.

![FontMaterial](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/FontMaterial_Get_Property.png?raw=true)

| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `ColorPrimary`
> Gets the **primary color**.

![ColorPrimary](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorPrimary_Get_Property.png?raw=true)

| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorSecondary`
> Gets the **secondary color**.

![ColorSecondary](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorSecondary_Get_Property.png?raw=true)

| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorAccent`
> Gets the **accent color**.

![ColorAccent](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ColorAccent_Get_Property.png?raw=true)

| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `Language`
> Gets the **current language code**.

![Language](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/Language_Get_Property.png?raw=true)

| Return | Type |
|--------|------|
| code   | text |

---

### 🟩 `ThemeMode`
> Gets the **current theme mode**.

![ThemeMode](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ThemeMode_Get_Property.png?raw=true)

| Return | Type |
|--------|------|
| mode   | text |


</details>

<details>
  <summary><kbd>Events</kbd></summary>


### 🟨 ErrorOccurred

>Occurs when an error happens

![ErrorOccurred Event](https://github.com/HB-Hridoy/th-m-l-t/blob/dev/out/blocks/ErrorOccurred_Event.png?raw=true)

| Parameter | Type
| - | - |
| errorFrom | text
| error | text

</details>

---

## 🚀 Usage

Get started with ThMLT in just **3 simple steps** — initialize once, format anywhere, and apply your style across the app.

---

### 1️⃣ 🟢 Initialize ThMLT (One-Time Setup)

Set up ThMLT with a single block — no repeated calls needed.

Use the `Initialize` block **on any screen** (commonly `Screen1`). ThMLT handles global data sync across all screens automatically.

> ✅ **Best Practice:** Call it inside the `Screen.Initialize` block for consistent behavior across the app.

![Initialize](out/blocks/UsageBlocks/Initialize_Method_On_Screen_Initialize.png)

🔧 Need help configuring your **Fonts**, **Colors**, or **Translations**?

📖 Follow the official [Data Configuration Guide →](https://github.com/HB-Hridoy/th-m-l-t/wiki/Guidelines#-data-configuration-guide) to set up your JSON schemas correctly.

---


### 2️⃣ 🧠 Format Your TextViews with Style

Use ThMLT's format syntax to apply translation, font, and color in one go — directly within a text string.

```text
[tag1,tag2,tag3]Visible Fallback Text
```

#### ✅ Example

```text
[name,regular,label]Welcome Back!
```

| Tag        | Purpose                                                                 |
|------------|-------------------------------------------------------------------------|
| `name`     | 🔤 Translation key (from your translations JSON)                        |
| `regular`  | 🔠 Font tag (matches `shortFontTag` in your Fonts JSON)                |
| `label`    | 🎨 Semantic color key (from your Themes JSON's `Semantic` section)     |
| `Welcome Back!` | 🪪 Visible fallback text if translation fails or `#` is used       |


#### 🎛️ Partial Formatting Control with `#`

You can **skip specific formatting layers** by using `#` in any of the tag slots inside your `[tag1,tag2,tag3]` syntax.

```text
[#,#,#]Text
```

Each `#` tells ThMLT to **ignore that particular formatting layer**:

| Tag Position | Meaning              | Example                 | Result                                                       |
|--------------|----------------------|-------------------------|--------------------------------------------------------------|
| 1st (`#`)    | Skip **Translation** | `[#,bold,label]Hello`   | Won’t translate; applies font & color. Applies fallback text |
| 2nd (`#`)    | Skip **Font**        | `[greeting,#,label]Hi`  | Uses translation & color, skips font                         |
| 3rd (`#`)    | Skip **Color**       | `[greeting,bold,#]Hi`   | Applies translation & font, no color                         |

> ✅ `[#,regular,label]Welcome Back!`  
> Skips translation → keeps visible text → applies font + color.

This gives you fine-grained control over how text is styled — even dynamically.

---

### 3️⃣ 🧰 Apply Formatting Block

Use the `ApplyFormatting` block to automatically scan all **TextViews inside an Arrangement** and apply formatting wherever the syntax is detected.

![Apply Formatting](out/blocks/UsageBlocks/ApplyFormattingBlock.png)

> 📍 **How it works:**
> - Loops through all text components
> - Detects `[tag1,tag2,tag3]` patterns
> - Applies font, color, and translation instantly

