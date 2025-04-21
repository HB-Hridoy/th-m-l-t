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
        Iterator<String> fontKeys = fontsNode.fieldNames();
        Set<String> uniqueFontKeys = new LinkedHashSet<>();
        Set<String> usedShortTags = new HashSet<>();
        List<String> keysToCheck = new ArrayList<>();
        Pattern shortTagPattern = Pattern.compile("^[a-zA-Z_]+$");
        Set<String> reservedShortTags = new HashSet<>(Arrays.asList("default", "null", "thmlt"));
        int fallbackCounter = 1;

        while (fontKeys.hasNext()) {
            keysToCheck.add(fontKeys.next());
        }

        for (String fontKey : keysToCheck) {
            if (uniqueFontKeys.contains(fontKey)) {
                result.errors.add("Duplicate font key: " + fontKey);
                continue;
            }
            uniqueFontKeys.add(fontKey);

            JsonNode fontObjNode = fontsNode.get(fontKey);
            if (!fontObjNode.isObject()) {
                result.warnings.add("Font '" + fontKey + "' is not an object. Replacing with default.");
                ObjectNode defaultFont = mapper.createObjectNode();
                String fallbackTag = "f" + (fallbackCounter++);
                defaultFont.put("shortFontTag", fallbackTag);
                defaultFont.put("fontName", "default.ttf");
                fontsNode.set(fontKey, defaultFont);
                usedShortTags.add(fallbackTag);
                continue;
            }

            ObjectNode fontObject = (ObjectNode) fontObjNode;

            // Validate or generate shortFontTag
            String shortTag = "";
            boolean validShortTag = fontObject.has("shortFontTag") && fontObject.get("shortFontTag").isTextual();
            if (validShortTag) {
                shortTag = fontObject.get("shortFontTag").asText();
                boolean validPattern = shortTagPattern.matcher(shortTag).matches();
                boolean validLength = shortTag.length() >= 1 && shortTag.length() <= 6;
                boolean notReserved = !reservedShortTags.contains(shortTag.toLowerCase());
                boolean notDuplicate = !usedShortTags.contains(shortTag);

                if (!(validPattern && validLength && notReserved && notDuplicate)) {
                    result.warnings.add("Invalid or duplicate shortFontTag '" + shortTag + "' in font '" + fontKey + "'. Replaced with fallback.");
                    validShortTag = false;
                }
            }

            if (!validShortTag) {
                shortTag = "f" + (fallbackCounter++);
                fontObject.put("shortFontTag", shortTag);
            }
            usedShortTags.add(shortTag);

            // Ensure fontName
            if (!fontObject.has("fontName") || !fontObject.get("fontName").isTextual()) {
                result.warnings.add("Missing fontName in font '" + fontKey + "'. Added default 'default.ttf'.");
                fontObject.put("fontName", "default.ttf");
            } else {
                String fontName = fontObject.get("fontName").asText();
                if (!(fontName.endsWith(".ttf") || fontName.endsWith(".otf"))) {
                    result.warnings.add("Invalid fontName extension in font '" + fontKey + "'. Must be .ttf or .otf. Replaced with 'default.ttf'.");
                    fontObject.put("fontName", "default.ttf");
                }
            }
        }

        result.correctedJson = corrected;
        return result;
    }

}
