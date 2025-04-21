
# 🧾 Fonts JSON Configuration Guidelines

This document describes the proper structure and rules for defining fonts in your ThMLT configuration.

---

## 📌 Basic Structure

```json
{
  "ProjectName": "ThMLT",
  "Author": "Hridoy",
  "Version": "1.0",
  "Fonts": {
    "regular": {
      "shortFontTag": "r",
      "fontName": "regular.ttf"
    },
    "bold": {
      "shortFontTag": "b",
      "fontName": "bold.otf"
    }
  }
}
```

---

## ✅ Required Field

- The JSON **must include** a top-level `Fonts` object.
- Each key inside `Fonts` (e.g., `regular`, `bold`) must be **unique** and represent a font style.

---

## 📦 Each Font Object Must Include:

| Property       | Type   | Description                                                             |
|----------------|--------|-------------------------------------------------------------------------|
| `shortFontTag` | String | A short alias/tag for the font (used in tools and references).         |
| `fontName`     | String | Font file name with `.ttf` or `.otf` extension.                        |

---

## 🔤 shortFontTag Rules

- ✅ Must consist only of **letters** and **underscores**: `[a-zA-Z_]`
- ✅ Length must be **between 1 and 6 characters**
- ❌ Cannot be one of the **reserved keywords**:
    - `default`
    - `null`
    - `thmlt`
- ⚠️ Duplicate `shortFontTag` values are **not allowed**
- If any of these rules are broken, a fallback like `f1`, `f2`, ... will be used

---

## 📁 fontName Rules

- Must be a valid string ending in:
    - `.ttf`
    - `.otf`
- File extension is **case-sensitive**
- Duplicates are allowed, but extension must be valid

---

## 💡 Example with All Rules Applied

```json
{
  "Fonts": {
    "regular": {
      "shortFontTag": "r",
      "fontName": "regular.ttf"
    },
    "display": {
      "shortFontTag": "disp_1",
      "fontName": "display.otf"
    },
    "caps": {
      "shortFontTag": "CAP",
      "fontName": "caps.ttf"
    }
  }
}
```

---

## ❌ Invalid Example (will be auto-corrected)

```json
{
  "Fonts": {
    "regular": {
      "fontName": "regular.otf"
    },
    "default": {
      "shortFontTag": "default",
      "fontName": "xyz.woff"
    },
    "caps": "notAnObject"
  }
}
```

➡ Will be corrected to:

```json
{
  "Fonts": {
    "regular": {
      "shortFontTag": "f1",
      "fontName": "regular.otf"
    },
    "default": {
      "shortFontTag": "f2",
      "fontName": "default.ttf"
    },
    "caps": {
      "shortFontTag": "f3",
      "fontName": "default.ttf"
    }
  }
}
```

---

## ✅ Best Practices

- Keep shortFontTags **short and descriptive**
- Use consistent naming for keys and font files

---

Created by: **Hridoy**  
Version: `3.0`
```