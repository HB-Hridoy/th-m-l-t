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
> 
![Initialize_Method](https://github.com/user-attachments/assets/e738b356-4d0d-4d65-85b6-6b77e3bfddef)

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

![ApplyFormatting_Method](https://github.com/user-attachments/assets/cc2e595a-448f-41bc-aee4-50af1e5c90db)


| Parameter | Type      |
|-----------|-----------|
| layout    | component |


---

### 🟪 `ApplyCustomizedFormatting`

> **Purpose:** Applies formatting to a layout using a **specific** theme and language.

![ApplyCustomizedFormatting_Method](https://github.com/user-attachments/assets/08c81585-94ab-4a32-a827-b916cd19fc15)


| Parameter     | Type |
|---------------|------|
| layout        | component |
| themeMode     | text |
| languageCode  | text |



---

### 🟪 `Get`

> **Purpose:** Retrieves a list of values for a given data category.

![Get_Method](https://github.com/user-attachments/assets/43b871ff-400c-46fd-8eb0-bd6976675b41)


| Parameter | Type |
|-----------|------|
| data      | All <sub>(helper enums)</sub> |

**Return:** `list`  
**Why:** Returns the complete list of items for enums like `PrimitiveKeys`, `SemanticKeys`, `ThemeModes`, `FontTags`, `FontShortTags`, `TranslationKeys`, `SupportedLanguages`.

---

### 🟪 `GetTranslation`

> **Purpose:** Returns a translation for a given key using the *active language*.

![GetTranslation_Method](https://github.com/user-attachments/assets/9aa0d7be-7960-42c4-9033-01d9ecb41705)


| Parameter      | Type |
|----------------|------|
| translationKey | text |

**Return:** `text`  
**Why:** Returns the corresponding translation string, or `Not Found` if missing.

---

### 🟪 `GetFont`

> **Purpose:** Returns a font name for a given key using the *tag*.

<img width="207" alt="GetFont_Method" src="https://github.com/user-attachments/assets/85db8b5f-d8c4-4add-82bf-c53af602476e" />


| Parameter      | Type |
|----------------|------|
| tag | text |

**Return:** `text`  
**Why:** Returns the corresponding font name, or `Font not found` if missing.

---

### 🟪 `GetTranslationForLanguage`

> **Purpose:** Returns a translation for a key in a **specific language**.

![GetTranslationForLanguage_Method](https://github.com/user-attachments/assets/167c2008-1c88-4200-bdf2-351dbbacea34)


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

![GetPrimitiveColor_Method](https://github.com/user-attachments/assets/e2005f38-662e-4fee-b679-cdd7e2a3a076)


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

![GetSemanticColorSource_Method](https://github.com/user-attachments/assets/951d3459-2a51-4a37-ab3d-e450e3c9e6cf)


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

![GetSemanticColor_Method](https://github.com/user-attachments/assets/69fdb272-a2dd-4ecd-a995-17bf5cb81118)


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

![GetSemanticColorByThemeMode_Method](https://github.com/user-attachments/assets/ce08673c-71c2-4871-b4c2-e4e3c66cd260)


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

![FontRegular_Set_Property](https://github.com/user-attachments/assets/0e5d9b18-dbb9-476f-91de-1dd9de94024a)


| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `FontBold`
> Sets the **bold font**.

![FontBold_Set_Property](https://github.com/user-attachments/assets/3ab4428f-c2e8-471e-a9f6-4c710654bf34)


| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `FontMaterial`
> Sets the **material icon font**.

![FontMaterial_Set_Property](https://github.com/user-attachments/assets/802babf6-199b-42a9-8bb6-b6c5be8c1131)


| Input | Type |
|-------|------|
| font  | text |

---

### 🟩 `ColorPrimary`
> Sets the **primary color**.

![ColorPrimary_Set_Property](https://github.com/user-attachments/assets/a0e00b40-0789-4878-96d0-50d9e56f527a)


| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorSecondary`
> Sets the **secondary color**.

![ColorSecondary_Set_Property](https://github.com/user-attachments/assets/107920b4-94cd-4ff3-874a-4b48a27089ff)


| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorAccent`
> Sets the **accent color**.

![ColorAccent_Set_Property](https://github.com/user-attachments/assets/a079db58-118c-4dee-a357-efcbe083a0e9)


| Input  | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `Language`
> Sets the **active translation language**.

![Language_Set_Property](https://github.com/user-attachments/assets/9e6b87fe-21b6-4a8a-8042-7df8041642f4)


| Input | Type |
|-------|------|
| code  | text |

---

### 🟩 `ThemeMode`
> Sets the **active theme mode** (e.g., `light`, `dark`).

![ThemeMode_Set_Property](https://github.com/user-attachments/assets/0dd8e315-6e0b-4302-bd63-735656924546)


| Input | Type |
|-------|------|
| mode  | text |

---

## 🔍 <kbd>Getters</kbd>

### 🟩 `FontRegular`
> Gets the **regular font**.

![FontRegular_Get_Property](https://github.com/user-attachments/assets/2495fe28-954d-435f-80b6-dcf116e7f482)


| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `FontBold`
> Gets the **bold font**.
> 
![FontBold_Get_Property](https://github.com/user-attachments/assets/e374ba29-c554-4266-84d8-6eaa045e9334)

| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `FontMaterial`
> Gets the **material font**.

![FontMaterial_Get_Property](https://github.com/user-attachments/assets/d1928dfa-83bb-44c1-892e-c92bd6df540a)


| Return | Type |
|--------|------|
| font   | text |

---

### 🟩 `ColorPrimary`
> Gets the **primary color**.

![ColorPrimary_Get_Property](https://github.com/user-attachments/assets/c7644bf2-ebec-43a9-8ce1-e6d91809daba)


| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorSecondary`
> Gets the **secondary color**.

![ColorSecondary_Get_Property](https://github.com/user-attachments/assets/d21e0bd7-7734-4a94-94bd-f89f1380638c)


| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `ColorAccent`
> Gets the **accent color**.

![ColorAccent_Get_Property](https://github.com/user-attachments/assets/756cbd8e-8c62-42e8-ad7c-a2c4ed8e39fc)


| Return | Type   |
|--------|--------|
| color  | number |

---

### 🟩 `Language`
> Gets the **current language code**.

![Language_Get_Property](https://github.com/user-attachments/assets/96e8a0df-fcb5-4713-835e-027fd74e4e03)


| Return | Type |
|--------|------|
| code   | text |

---

### 🟩 `ThemeMode`
> Gets the **current theme mode**.

![ThemeMode_Get_Property](https://github.com/user-attachments/assets/e65a704f-87be-4720-8296-baddf934f525)


| Return | Type |
|--------|------|
| mode   | text |


</details>

<details>
  <summary><kbd>Events</kbd></summary>


### 🟨 ErrorOccurred

>Occurs when an error happens

![ErrorOccurred_Event](https://github.com/user-attachments/assets/82d2c986-618a-4f23-ae60-c18196e3c140)


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

<img width="612" alt="Initialize_Method_On_Screen_Initialize" src="https://github.com/user-attachments/assets/888232b1-47cd-491a-a537-63794ef398a4" />


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

<img width="428" alt="ApplyFormattingBlock" src="https://github.com/user-attachments/assets/d0bff0f3-460b-463d-ab59-384e4c61acb8" />

> 📍 **How it works:**
> - Loops through all text components
> - Detects `[tag1,tag2,tag3]` patterns
> - Applies font, color, and translation instantly

