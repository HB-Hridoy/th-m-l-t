
# 🧩 ThMLT JSON Schema Guide

This guide helps you write valid ThMLT configuration JSON.

---

## 📦 Example Configuration

```json
{
  "ProjectName": "exTest",
  "Version": "1.0",
  "Author": "Hridoy",
  "Modes": ["Light", "Dark"],
  "DefaultMode": "Light",
  "Primitives": {
    "black": "#000000",
    "white": "#FFFFFF",
    "pink": "#FE00FF"
  },
  "Semantic": {
    "Light": {
      "surface-primary": "white",
      "label": "black"
    },
    "Dark": {
      "surface-primary": "black",
      "label": "white"
    }
  }
}
```

---

## ✅ Required Fields

| Field         | Type    | Description                          |
|---------------|---------|--------------------------------------|
| Modes         | Array   | List of visual modes (e.g. Light/Dark) |
| DefaultMode   | String  | The default mode, must be in `Modes` |
| Primitives    | Object  | Key-value map of named colors        |
| Semantic      | Object  | Mapping of modes to color references |

---

## 🎯 Validation Rules

- **Modes** must be a list of **unique strings**.
- **DefaultMode** must match one of the values in `Modes`.
- **Primitives** must contain keys with valid **hex color codes** (`#RRGGBB`).
- **Semantic** keys must match the values in `Modes`.
- Every **semantic color** must match a key in `Primitives`.
- All **semantic modes** must contain the same keys.

---

## ⚠️ Auto-fix Behavior

- Missing fields are added with default values.
- Invalid colors are replaced with `#FFFFFF`.
- Missing semantic entries are created with fallback values.
- Invalid semantic references are replaced with the first primitive key.

