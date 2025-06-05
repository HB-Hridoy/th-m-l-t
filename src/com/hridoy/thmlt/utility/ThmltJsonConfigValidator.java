package com.hridoy.thmlt.utility;

import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shaded.fasterxml.jackson.databind.node.ArrayNode;
import com.shaded.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ThmltJsonConfigValidator {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public static class ValidationResult {
        public ObjectNode correctedJson;
        public List<String> errors = new ArrayList<>();
        public List<String> warnings = new ArrayList<>();
    }

    public static ValidationResult validateThmltJson(String jsonInput) throws IOException {
        JsonNode rootNode = mapper.readTree(jsonInput);
        ObjectNode corrected = rootNode.deepCopy();
        ValidationResult result = new ValidationResult();

        // Required fields
        List<String> requiredFields = Arrays.asList("Modes", "DefaultMode", "Primitives", "Semantic");
        for (String field : requiredFields) {
            if (!corrected.has(field)) {
                result.errors.add("Missing required field: " + field);
                if (field.equals("Modes")) {
                    corrected.set("Modes", mapper.createArrayNode());
                } else if (field.equals("DefaultMode")) {
                    corrected.put("DefaultMode", "");
                } else if (field.equals("Primitives")) {
                    corrected.set("Primitives", mapper.createObjectNode());
                } else if (field.equals("Semantic")) {
                    corrected.set("Semantic", mapper.createObjectNode());
                }
            }
        }

        // Unique modes
        Set<String> uniqueModes = new LinkedHashSet<>();
        ArrayNode modesArray = corrected.withArray("Modes");
        for (int i = 0; i < modesArray.size(); i++) {
            JsonNode mode = modesArray.get(i);
            if (mode.isTextual()) {
                uniqueModes.add(mode.asText());
            }
        }
        ArrayNode newModesArray = mapper.createArrayNode();
        for (String mode : uniqueModes) {
            newModesArray.add(mode);
        }
        corrected.set("Modes", newModesArray);

        // Validate DefaultMode
        String defaultMode = corrected.path("DefaultMode").asText();
        if (!uniqueModes.contains(defaultMode)) {
            result.errors.add("DefaultMode '" + defaultMode + "' not found in Modes.");
        }

        // Validate Primitives
        ObjectNode primitives = corrected.with("Primitives");
        List<String> primitiveKeys = new ArrayList<>();
        Iterator<String> primFields = primitives.fieldNames();
        while (primFields.hasNext()) {
            primitiveKeys.add(primFields.next());
        }

        for (String key : primitiveKeys) {
            JsonNode value = primitives.get(key);
            if (!value.isTextual() || !HEX_COLOR_PATTERN.matcher(value.asText()).matches()) {
                result.warnings.add("Invalid color for '" + key + "' in Primitives. Replaced with #FFFFFF.");
                primitives.put(key, "#FFFFFF");
            }
        }

        if (primitiveKeys.isEmpty()) {
            primitiveKeys.add("white");
            primitives.put("white", "#FFFFFF");
        }

        // Validate Semantic
        ObjectNode semantic = corrected.with("Semantic");
        String firstPrimitiveKey = primitiveKeys.get(0);
        ObjectNode firstModeNode = null;
        Set<String> semanticKeys = new LinkedHashSet<>();

        for (String mode : uniqueModes) {
            if (semantic.has(mode) && semantic.get(mode).isObject()) {
                firstModeNode = (ObjectNode) semantic.get(mode);
                Iterator<String> keys = firstModeNode.fieldNames();
                while (keys.hasNext()) {
                    semanticKeys.add(keys.next());
                }
                break;
            }
        }

        for (String mode : uniqueModes) {
            if (!semantic.has(mode)) {
                result.errors.add("Missing semantic mode: " + mode);
                ObjectNode newMode = mapper.createObjectNode();
                for (String key : semanticKeys) {
                    newMode.put(key, firstPrimitiveKey);
                }
                semantic.set(mode, newMode);
            }
        }

        for (String mode : uniqueModes) {
            ObjectNode modeObj = semantic.with(mode);
            Set<String> currentKeys = new HashSet<>();
            Iterator<String> keys = modeObj.fieldNames();
            while (keys.hasNext()) {
                currentKeys.add(keys.next());
            }

            for (String key : currentKeys) {
                JsonNode val = modeObj.get(key);
                if (!val.isTextual() || !primitiveKeys.contains(val.asText())) {
                    result.warnings.add("Invalid semantic value for '" + key + "' in mode '" + mode + "'. Replaced with '" + firstPrimitiveKey + "'.");
                    modeObj.put(key, firstPrimitiveKey);
                }
            }

            for (String requiredKey : semanticKeys) {
                if (!modeObj.has(requiredKey)) {
                    result.warnings.add("Missing key '" + requiredKey + "' in mode '" + mode + "'. Added with default value '" + firstPrimitiveKey + "'.");
                    modeObj.put(requiredKey, firstPrimitiveKey);
                }
            }

            Iterator<String> modeFieldKeys = modeObj.fieldNames();
            while (modeFieldKeys.hasNext()) {
                semanticKeys.add(modeFieldKeys.next());
            }
        }

        result.correctedJson = corrected;
        return result;
    }

    public static ValidationResult validateFontsJson(String jsonInput) throws IOException {
        JsonNode rootNode = mapper.readTree(jsonInput);
        ObjectNode corrected = rootNode.deepCopy();
        ValidationResult result = new ValidationResult();

        if (!corrected.has("Fonts") || !corrected.get("Fonts").isObject()) {
            result.errors.add("Missing required field: Fonts");
            corrected.set("Fonts", mapper.createObjectNode());
        }

        ObjectNode fontsNode = corrected.with("Fonts");
        Iterator<String> fontNames = fontsNode.fieldNames();
        Set<String> seenFontNames = new LinkedHashSet<>();
        List<String> keysToCheck = new ArrayList<>();
        Pattern fontNamePattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]{0,31}$");
        Set<String> reservedFontNames = new HashSet<>(Arrays.asList("default", "null", "thmlt"));
        int fallbackCounter = 1;

        while (fontNames.hasNext()) {
            keysToCheck.add(fontNames.next());
        }

        for (String fontName : keysToCheck) {
            if (seenFontNames.contains(fontName)) {
                result.errors.add("Duplicate font name: " + fontName);
                fontsNode.remove(fontName);
                continue;
            }

            boolean validKeyPattern = fontNamePattern.matcher(fontName).matches();
            boolean notReserved = !reservedFontNames.contains(fontName.toLowerCase());

            String originalValue = fontsNode.get(fontName).isTextual() ? fontsNode.get(fontName).asText() : null;
            if (!validKeyPattern || !notReserved) {
                String fallbackKey;
                do {
                    fallbackKey = "f" + (fallbackCounter++);
                } while (seenFontNames.contains(fallbackKey));
                fontsNode.remove(fontName);
                fontsNode.put(fallbackKey, originalValue != null ? originalValue : "default.ttf");
                result.warnings.add("Invalid or reserved font name '" + fontName + "'. Replaced with '" + fallbackKey + "'.");
                fontName = fallbackKey;
            }

            seenFontNames.add(fontName);
            JsonNode fontValue = fontsNode.get(fontName);

            if (!fontValue.isTextual()) {
                result.warnings.add("Font value for '" + fontName + "' must be a string. Replaced with 'default.ttf'.");
                fontsNode.put(fontName, "default.ttf");
                continue;
            }

            String fontFileName = fontValue.asText();
            if (!(fontFileName.endsWith(".ttf") || fontFileName.endsWith(".otf"))) {
                result.warnings.add("Invalid font file extension for '" + fontName + "'. Must be .ttf or .otf. Replaced with 'default.ttf'.");
                fontsNode.put(fontName, "default.ttf");
            }
        }

        result.correctedJson = corrected;
        return result;
    }

    public static ValidationResult validateTranslationsJson(String jsonInput) throws IOException {
        JsonNode rootNode = mapper.readTree(jsonInput);
        ObjectNode corrected = rootNode.deepCopy();
        ValidationResult result = new ValidationResult();

        // Validate SupportedLanguages
        if (!corrected.has("SupportedLanguages") || !corrected.get("SupportedLanguages").isArray()) {
            result.errors.add("Missing or invalid field: SupportedLanguages");
            corrected.set("SupportedLanguages", mapper.createArrayNode());
        }

        ArrayNode supportedLangsNode = corrected.withArray("SupportedLanguages");
        Set<String> uniqueLangs = new LinkedHashSet<>();
        for (JsonNode lang : supportedLangsNode) {
            if (lang.isTextual()) {
                uniqueLangs.add(lang.asText());
            }
        }

        ArrayNode newLangs = mapper.createArrayNode();
        for (String lang : uniqueLangs) {
            newLangs.add(lang);
        }
        corrected.set("SupportedLanguages", newLangs);

        // Validate DefaultLanguage
        String defaultLang = corrected.path("DefaultLanguage").asText();
        if (!uniqueLangs.contains(defaultLang)) {
            result.errors.add("DefaultLanguage '" + defaultLang + "' is not in SupportedLanguages.");
            if (!uniqueLangs.isEmpty()) {
                corrected.put("DefaultLanguage", uniqueLangs.iterator().next());
            } else {
                corrected.put("DefaultLanguage", "");
            }
        }

        // Validate Translations
        if (!corrected.has("Translations") || !corrected.get("Translations").isObject()) {
            result.errors.add("Missing or invalid field: Translations");
            corrected.set("Translations", mapper.createObjectNode());
        }

        ObjectNode translations = corrected.with("Translations");
        Iterator<String> keys = translations.fieldNames();
        List<String> translationKeys = new ArrayList<>();
        while (keys.hasNext()) {
            translationKeys.add(keys.next());
        }

        for (String key : translationKeys) {
            JsonNode val = translations.get(key);
            if (!val.isObject()) {
                result.warnings.add("Translation '" + key + "' is not an object. Replacing with default map.");
                ObjectNode fallback = mapper.createObjectNode();
                for (String lang : uniqueLangs) {
                    fallback.put(lang, "Not Found");
                }
                translations.set(key, fallback);
                continue;
            }

            ObjectNode translationObj = (ObjectNode) val;
            for (String lang : uniqueLangs) {
                if (!translationObj.has(lang)) {
                    result.warnings.add("Missing language '" + lang + "' in translation for key '" + key + "'. Added with default text.");
                    translationObj.put(lang, "Not Found");
                }
            }
        }

        result.correctedJson = corrected;
        return result;
    }


}
