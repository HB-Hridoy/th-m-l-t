

# 📘 ThMLT Translations JSON Structure Guideline

This document explains how to properly structure your **translations JSON** so it's compatible with the ThMLT extension for AI2.

---

## ✅ Required Fields

| Field              | Type     | Description                                                         |
|--------------------|----------|---------------------------------------------------------------------|
| `SupportedLanguages` | Array    | List of supported language codes (e.g. `"en"`, `"bn"`, `"fr"`)     |
| `DefaultLanguage`   | String   | The default language, must exist in `SupportedLanguages`           |
| `Translations`      | Object   | Contains keys and translations for each language                   |

---

## 📄 Full Example

```json
{
  "ProjectName": "MyProject",
  "Author": "Jane Doe",
  "Version": "1.0",
  "SupportedLanguages": ["en", "bn", "es", "fr", "de"],
  "DefaultLanguage": "en",
  "Translations": {
    "home": {
      "en": "Home",
      "bn": "বাড়ি",
      "es": "Inicio",
      "fr": "Accueil",
      "de": "Startseite"
    },
    "discover": {
      "en": "Discover",
      "bn": "আবিষ্কার করুন",
      "es": "Descubrir",
      "fr": "Découvrir",
      "de": "Entdecken"
    }
  }
}
```

---

## ⚠️ Validation Rules

### 1. `SupportedLanguages`
- Must be a **non-empty array** of **unique language codes**.
- Example:  
  ✅ `["en", "bn", "fr"]`  
  ❌ `["en", "en", "fr"]` _(duplicate)_

### 2. `DefaultLanguage`
- Must be one of the languages listed in `SupportedLanguages`.
- ❌ Invalid:
  ```json
  "SupportedLanguages": ["en", "fr"],
  "DefaultLanguage": "de" // invalid
  ```

### 3. `Translations`
- Each key in `Translations` must be an object containing translations for **all supported languages**.
- Missing languages will be auto-filled with `"Not Found"` (and a warning will be generated).
- ✅ Good:
  ```json
  "Translations": {
    "title": {
      "en": "Title",
      "bn": "শিরোনাম",
      "fr": "Titre"
    }
  }
  ```
- ❌ Bad:
  ```json
  "Translations": {
    "title": {
      "en": "Title"
    }
  }
  ```

---

## 🧪 Validation Behavior

When you run the validator:
- Duplicates in `SupportedLanguages` will be removed.
- Missing translations will be filled with `"Not Found"`.
- Critical issues will appear under `errors`, and patches under `warnings`.
- The corrected JSON will be available for saving or use.
