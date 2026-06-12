package com.hridoy.thmlt.utility;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Pattern;

import static com.hridoy.thmlt.ThMLT.TAG;

public class ThmltJsonConfigValidator {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public static class ValidationResult {
        public JSONObject correctedJson;
        public List<String> errors = new ArrayList<>();
        public List<String> warnings = new ArrayList<>();

        public ValidationResult() {
            this.correctedJson = new JSONObject();
        }
    }

    public static ValidationResult validateThmltJson(String jsonInput) {
        ValidationResult result = new ValidationResult();

        try {
            JSONObject rootNode = new JSONObject(jsonInput);
            result.correctedJson = deepCopy(rootNode);

            // Required fields validation
            String[] requiredFields = {"Modes", "DefaultMode", "Primitives", "Semantic"};
            for (String field : requiredFields) {
                if (!result.correctedJson.has(field)) {
                    result.errors.add("Missing required field: " + field);
                    addDefaultField(result.correctedJson, field);
                }
            }

            // Process unique modes
            Set<String> uniqueModes = processUniqueModesArray(result);

            // Validate DefaultMode
            validateDefaultMode(result, uniqueModes);

            // Validate Primitives
            List<String> primitiveKeys = validatePrimitives(result);

            // Validate Semantic
            validateSemantic(result, uniqueModes, primitiveKeys);

        } catch (JSONException e) {
            result.errors.add("Invalid JSON format: " + e.getMessage());
            LogMessage.e("JSON parsing error: " + e.getMessage());
        }

        return result;
    }

    public static ValidationResult validateTypographyJson(String jsonInput) {
        LogMessage.d("Starting validation of Typography JSON");
        ValidationResult result = new ValidationResult();

        try {
            JSONObject rootNode = new JSONObject(jsonInput);
            result.correctedJson = deepCopy(rootNode);

            // Validate Fonts
            Set<String> validFontKeys = validateFonts(result);

            // Validate Typographies
            validateTypographies(result, validFontKeys);

            LogMessage.d("Validation complete. Errors: " + result.errors.size() + ", Warnings: " + result.warnings.size());

        } catch (JSONException e) {
            result.errors.add("Invalid JSON format: " + e.getMessage());
            LogMessage.e("JSON parsing error: " + e.getMessage());
        }

        return result;
    }

    public static ValidationResult validateTranslationsJson(String jsonInput) {
        ValidationResult result = new ValidationResult();

        try {
            JSONObject rootNode = new JSONObject(jsonInput);
            result.correctedJson = deepCopy(rootNode);

            // Validate SupportedLanguages
            Set<String> uniqueLangs = validateSupportedLanguages(result);

            // Validate DefaultLanguage
            validateDefaultLanguage(result, uniqueLangs);

            // Validate Translations
            validateTranslations(result, uniqueLangs);

        } catch (JSONException e) {
            result.errors.add("Invalid JSON format: " + e.getMessage());
            LogMessage.e("JSON parsing error: " + e.getMessage());
        }

        return result;
    }

    // Helper methods
    private static JSONObject deepCopy(JSONObject original) throws JSONException {
        return new JSONObject(original.toString());
    }

    private static void addDefaultField(JSONObject json, String field) throws JSONException {
        switch (field) {
            case "Modes":
                json.put(field, new JSONArray());
                break;
            case "DefaultMode":
                json.put(field, "");
                break;
            case "Primitives":
            case "Semantic":
                json.put(field, new JSONObject());
                break;
        }
    }

    private static Set<String> processUniqueModesArray(ValidationResult result) throws JSONException {
        Set<String> uniqueModes = new LinkedHashSet<>();
        JSONArray modesArray = result.correctedJson.optJSONArray("Modes");

        if (modesArray != null) {
            for (int i = 0; i < modesArray.length(); i++) {
                Object mode = modesArray.opt(i);
                if (mode instanceof String) {
                    uniqueModes.add((String) mode);
                }
            }
        }

        // Rebuild modes array with unique values
        JSONArray newModesArray = new JSONArray();
        for (String mode : uniqueModes) {
            newModesArray.put(mode);
        }
        result.correctedJson.put("Modes", newModesArray);

        return uniqueModes;
    }

    private static void validateDefaultMode(ValidationResult result, Set<String> uniqueModes) throws JSONException {
        String defaultMode = result.correctedJson.optString("DefaultMode", "");
        if (!uniqueModes.contains(defaultMode)) {
            result.errors.add("DefaultMode '" + defaultMode + "' not found in Modes.");
        }
    }

    private static List<String> validatePrimitives(ValidationResult result) throws JSONException {
        JSONObject primitives = result.correctedJson.optJSONObject("Primitives");
        if (primitives == null) {
            primitives = new JSONObject();
            result.correctedJson.put("Primitives", primitives);
        }

        List<String> primitiveKeys = new ArrayList<>();
        Iterator<String> keys = primitives.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String value = primitives.optString(key, "");

            if (!HEX_COLOR_PATTERN.matcher(value).matches()) {
                result.warnings.add("Invalid color for '" + key + "' in Primitives. Replaced with #FFFFFF.");
                primitives.put(key, "#FFFFFF");
            }
            primitiveKeys.add(key);
        }

        if (primitiveKeys.isEmpty()) {
            primitiveKeys.add("white");
            primitives.put("white", "#FFFFFF");
        }

        return primitiveKeys;
    }

    private static void validateSemantic(ValidationResult result, Set<String> uniqueModes, List<String> primitiveKeys) throws JSONException {
        JSONObject semantic = result.correctedJson.optJSONObject("Semantic");
        if (semantic == null) {
            semantic = new JSONObject();
            result.correctedJson.put("Semantic", semantic);
        }

        String firstPrimitiveKey = primitiveKeys.isEmpty() ? "white" : primitiveKeys.get(0);
        Set<String> semanticKeys = new LinkedHashSet<>();

        // Collect all semantic keys from existing modes
        for (String mode : uniqueModes) {
            JSONObject modeObj = semantic.optJSONObject(mode);
            if (modeObj != null) {
                Iterator<String> keys = modeObj.keys();
                while (keys.hasNext()) {
                    semanticKeys.add(keys.next());
                }
                break; // Only need first valid mode to establish keys
            }
        }

        // Create missing modes
        for (String mode : uniqueModes) {
            if (!semantic.has(mode)) {
                result.errors.add("Missing semantic mode: " + mode);
                JSONObject newMode = new JSONObject();
                for (String key : semanticKeys) {
                    newMode.put(key, firstPrimitiveKey);
                }
                semantic.put(mode, newMode);
            }
        }

        // Validate and fix each mode
        for (String mode : uniqueModes) {
            JSONObject modeObj = semantic.optJSONObject(mode);
            if (modeObj == null) continue;

            // Validate existing keys
            Iterator<String> keys = modeObj.keys();
            List<String> keyList = new ArrayList<>();
            while (keys.hasNext()) {
                keyList.add(keys.next());
            }

            for (String key : keyList) {
                String value = modeObj.optString(key, "");
                if (!primitiveKeys.contains(value)) {
                    result.warnings.add("Invalid semantic value for '" + key + "' in mode '" + mode + "'. Replaced with '" + firstPrimitiveKey + "'.");
                    modeObj.put(key, firstPrimitiveKey);
                }
            }

            // Add missing keys
            for (String requiredKey : semanticKeys) {
                if (!modeObj.has(requiredKey)) {
                    result.warnings.add("Missing key '" + requiredKey + "' in mode '" + mode + "'. Added with default value '" + firstPrimitiveKey + "'.");
                    modeObj.put(requiredKey, firstPrimitiveKey);
                }
            }

            // Update semantic keys set
            Iterator<String> modeKeys = modeObj.keys();
            while (modeKeys.hasNext()) {
                semanticKeys.add(modeKeys.next());
            }
        }
    }

    private static Set<String> validateFonts(ValidationResult result) throws JSONException {
        JSONObject fontsNode = result.correctedJson.optJSONObject("Fonts");
        if (fontsNode == null) {
            logError("Missing required field: Fonts", result);
            fontsNode = new JSONObject();
            result.correctedJson.put("Fonts", fontsNode);
        }

        Set<String> validFontKeys = new HashSet<>();
        Iterator<String> fontFields = fontsNode.keys();
        List<String> fontKeysList = new ArrayList<>();

        while (fontFields.hasNext()) {
            fontKeysList.add(fontFields.next());
        }

        for (String fontName : fontKeysList) {
            String fontValue = fontsNode.optString(fontName, "");

            if (fontValue.isEmpty()) {
                logWarning("Font '" + fontName + "' must have a string value. Replaced with 'default.ttf'.", result);
                fontsNode.put(fontName, "default.ttf");
            } else if (!(fontValue.endsWith(".ttf") || fontValue.endsWith(".otf"))) {
                logWarning("Font file for '" + fontName + "' must be .ttf or .otf. Replaced with 'default.ttf'.", result);
                fontsNode.put(fontName, "default.ttf");
            }
            validFontKeys.add(fontName);
        }

        return validFontKeys;
    }

    private static void validateTypographies(ValidationResult result, Set<String> validFontKeys) throws JSONException {
        JSONObject typoNode = result.correctedJson.optJSONObject("Typographies");
        if (typoNode == null) {
            logError("Missing required field: Typographies", result);
            typoNode = new JSONObject();
            result.correctedJson.put("Typographies", typoNode);
        }

        Iterator<String> typoKeys = typoNode.keys();
        List<String> typoKeysList = new ArrayList<>();
        while (typoKeys.hasNext()) {
            typoKeysList.add(typoKeys.next());
        }

        for (String typoName : typoKeysList) {
            JSONObject typo = typoNode.optJSONObject(typoName);

            if (typo == null) {
                logError("Typography '" + typoName + "' is not an object and was removed.", result);
                typoNode.remove(typoName);
                continue;
            }

            // Validate linkedFont
            String linkedFont = typo.optString("linkedFont", "");
            if (!validFontKeys.contains(linkedFont)) {
                logError("Typography '" + typoName + "' references invalid linkedFont: '" + linkedFont + "'. Set to first available font.", result);

                if (!validFontKeys.isEmpty()) {
                    String fallback = validFontKeys.iterator().next();
                    typo.put("linkedFont", fallback);
                } else {
                    typo.put("linkedFont", "default");
                    JSONObject fontsNode = result.correctedJson.optJSONObject("Fonts");
                    if (fontsNode != null) {
                        fontsNode.put("default", "default.ttf");
                        validFontKeys.add("default");
                    }
                }
            }

            // Validate numeric properties
            String[] numericProps = {"fontSize", "lineHeight", "letterSpacing"};
            for (String prop : numericProps) {
                if (!typo.has(prop) || typo.optString(prop, "").isEmpty()) {
                    logWarning("Typography '" + typoName + "' is missing or has empty '" + prop + "'. Set to '0'.", result);
                    typo.put(prop, "0");
                }
            }
        }
    }

    private static Set<String> validateSupportedLanguages(ValidationResult result) throws JSONException {
        JSONArray supportedLangsArray = result.correctedJson.optJSONArray("SupportedLanguages");
        if (supportedLangsArray == null) {
            result.errors.add("Missing or invalid field: SupportedLanguages");
            supportedLangsArray = new JSONArray();
            result.correctedJson.put("SupportedLanguages", supportedLangsArray);
        }

        Set<String> uniqueLangs = new LinkedHashSet<>();
        for (int i = 0; i < supportedLangsArray.length(); i++) {
            Object lang = supportedLangsArray.opt(i);
            if (lang instanceof String) {
                uniqueLangs.add((String) lang);
            }
        }

        // Rebuild array with unique languages
        JSONArray newLangs = new JSONArray();
        for (String lang : uniqueLangs) {
            newLangs.put(lang);
        }
        result.correctedJson.put("SupportedLanguages", newLangs);

        return uniqueLangs;
    }

    private static void validateDefaultLanguage(ValidationResult result, Set<String> uniqueLangs) throws JSONException {
        String defaultLang = result.correctedJson.optString("DefaultLanguage", "");
        if (!uniqueLangs.contains(defaultLang)) {
            result.errors.add("DefaultLanguage '" + defaultLang + "' is not in SupportedLanguages.");
            if (!uniqueLangs.isEmpty()) {
                result.correctedJson.put("DefaultLanguage", uniqueLangs.iterator().next());
            } else {
                result.correctedJson.put("DefaultLanguage", "");
            }
        }
    }

    private static void validateTranslations(ValidationResult result, Set<String> uniqueLangs) throws JSONException {
        JSONObject translations = result.correctedJson.optJSONObject("Translations");
        if (translations == null) {
            result.errors.add("Missing or invalid field: Translations");
            translations = new JSONObject();
            result.correctedJson.put("Translations", translations);
        }

        Iterator<String> keys = translations.keys();
        List<String> translationKeys = new ArrayList<>();
        while (keys.hasNext()) {
            translationKeys.add(keys.next());
        }

        for (String key : translationKeys) {
            JSONObject translationObj = translations.optJSONObject(key);

            if (translationObj == null) {
                result.warnings.add("Translation '" + key + "' is not an object. Replacing with default map.");
                JSONObject fallback = new JSONObject();
                for (String lang : uniqueLangs) {
                    fallback.put(lang, "Not Found");
                }
                translations.put(key, fallback);
                continue;
            }

            // Ensure all supported languages are present
            for (String lang : uniqueLangs) {
                if (!translationObj.has(lang)) {
                    result.warnings.add("Missing language '" + lang + "' in translation for key '" + key + "'. Added with default text.");
                    translationObj.put(lang, "Not Found");
                }
            }
        }
    }

    private static void logError(String msg, ValidationResult result) {
        result.errors.add(msg);
        LogMessage.e(msg);
    }

    private static void logWarning(String msg, ValidationResult result) {
        result.warnings.add(msg);
        LogMessage.w(msg);
    }
}