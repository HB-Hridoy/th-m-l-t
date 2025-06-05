package com.hridoy.thmlt;

import com.google.appinventor.components.runtime.repackaged.org.json.JSONArray;
import com.google.appinventor.components.runtime.repackaged.org.json.JSONException;
import com.google.appinventor.components.runtime.repackaged.org.json.JSONObject;
import com.google.appinventor.components.runtime.util.YailDictionary;
import com.hridoy.thmlt.helpers.All;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.hridoy.thmlt.utility.ThmltJsonConfigValidator;
import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.node.ArrayNode;
import com.shaded.fasterxml.jackson.databind.node.ObjectNode;

import android.util.Log;

import java.io.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DesignerComponent(
	version = 62,
	versionName = "3",
	description = "Extension component for ThMLT. Created using FAST CLI.",
	iconName = "icon.png"
)
public class ThMLT extends AndroidNonvisibleComponent {


  private static final String TAG = "ThMLT";
  private static String mFontRegular = "";
  private static String mFontBold = "";
  private static String mFontMaterial = "";

  private static int mColorPrimary = 0;
  private static int mColorSecondary = 0;
  private static int mColorAccent = 0;

  private static String ACTIVE_TRANSLATION_LANGUAGE = "";
  private static HashMap<String, String> ACTIVE_TRANSLATION_LANGUAGE_MAP = new HashMap<>();

  private static List<String> THEME_MODES = new ArrayList<>();
  private static String ACTIVE_THEME_MODE = "";
  private static HashMap<String, Integer> ACTIVE_THEME_MODE_COLOR_MAP = new HashMap<>();

  private static HashMap<String, Integer> PRIMITIVE_COLORS = new HashMap<>();
  private static HashMap<String, HashMap<String, String>> SEMANTIC_COLORS_SOURCE = new HashMap<>();
  private static HashMap<String, HashMap<String, Integer>> SEMANTIC_COLORS = new HashMap<>();

  private static HashMap<String, String> FONTS = new HashMap<>();

  private static List<String> supportedLanguages = new ArrayList<>();
  private static HashMap<String, HashMap<String, String>> translations = new HashMap<>();

//  ========== VERSION 2.1 VARIABLES START ===========  //

  private static String mFontItalic = "";

  private static String mTranslationLanguage = "";


  private Typeface fontTypeface;

  private static HashMap<String, Integer> colorMapV2 = new HashMap<>();
  private static HashMap<String, String> fontMapV2 = new HashMap<>();

  private static List<String> supportedLanguagesV2 = new ArrayList<>();
  private static HashMap<String, HashMap<String, String>> translationsV2 = new HashMap<>();


  private static HashMap<String, JSONObject> translationMapV2 = new HashMap<>();

//  ========== VERSION 2.1 VARIABLES END ===========  //



  private boolean IS_REPL = false;
  private final Context context;

  public ThMLT(ComponentContainer container) {
    super(container.$form());
    this.context = container.$context();
    if (this.form instanceof ReplForm) {
      this.IS_REPL = true;
    }
  }
  //---------------------------------------------------------------------------
  // Properties
  //---------------------------------------------------------------------------
  @SimpleProperty(description = "The regular font style")
  public String FontRegular() {
    return mFontRegular;
  }
  @SimpleProperty(description = "The regular font style")
  public void FontRegular(String fontRegular) {
    mFontRegular = fontRegular;
  }

  @SimpleProperty(description = "The bold font style")
  public String FontBold() {
    return mFontBold;
  }
  @SimpleProperty(description = "The bold font style")
  public void FontBold(String fontBold) {
    mFontBold = fontBold;
  }

  @SimpleProperty(description = "The material font style")
  public String FontMaterial() {
    return mFontMaterial;
  }
  @SimpleProperty(description = "The material font style")
  public void FontMaterial(String fontMaterial) {
    mFontMaterial = fontMaterial;
  }

  @SimpleProperty(description = "The primary color")
  public int ColorPrimary() {
    return mColorPrimary;
  }
  @SimpleProperty(description = "The primary color")
  public void ColorPrimary(int colorPrimary) {
    mColorPrimary = colorPrimary;
  }

  @SimpleProperty(description = "The secondary color")
  public int ColorSecondary() {
    return mColorSecondary;
  }
  @SimpleProperty(description = "The secondary color")
  public void ColorSecondary(int colorSecondary) {
    mColorSecondary = colorSecondary;
  }

  @SimpleProperty(description = "The accent color")
  public int ColorAccent() {
    return mColorAccent;
  }
  @SimpleProperty(description = "The accent color")
  public void ColorAccent(int colorAccent) {
    mColorAccent = colorAccent;
  }

  @SimpleProperty(description = "Returns the language code of the translation")
  public String Language(){
    return ACTIVE_TRANSLATION_LANGUAGE;
  }
  @SimpleProperty(description = "Sets the target language for translation")
  public void Language(String languageCode){
    if (supportedLanguages.contains(languageCode)) {
      ACTIVE_TRANSLATION_LANGUAGE = languageCode;
      ACTIVE_TRANSLATION_LANGUAGE_MAP = translations.get(languageCode);
    } else{
      ErrorOccurred("Language", "Error: Language '" + languageCode + "' is not supported.");
    }
  }

  @SimpleProperty(description = "Returns the active theme mode (e.g., light or dark)")
  public String ThemeMode(){
    return ACTIVE_THEME_MODE;
  }
  @SimpleProperty(description = "Sets the active theme mode (e.g., light or dark)")
  public void ThemeMode(String mode){
    if (THEME_MODES.contains(mode) && SEMANTIC_COLORS.containsKey(mode)){
      ACTIVE_THEME_MODE = mode;
      ACTIVE_THEME_MODE_COLOR_MAP = SEMANTIC_COLORS.get(ACTIVE_THEME_MODE);
    }
  }

  //---------------------------------------------------------------------------
  //Events
  //---------------------------------------------------------------------------

  @SimpleEvent(description = "Occurs when an error happens")
  public void ErrorOccurred(String errorFrom, String error) {
    EventDispatcher.dispatchEvent(this, "ErrorOccurred", errorFrom, error);
  }

  //---------------------------------------------------------------------------
  //Methods
  //---------------------------------------------------------------------------

  @SimpleFunction(description = "Initializes the extension with color themes, fonts, and translations. " +
          "Each parameter must be a valid JSON string or a .json file name from the assets.")
  public void Initialize(@Asset({".json"}) String colorThemes, @Asset({".json"})  String fonts, @Asset({".json"})  String translations) {
    parseInitializationInput("ColorThemes", colorThemes, "colorThemes");
    parseInitializationInput("Fonts", fonts, "fonts");
    parseInitializationInput("Translations", translations, "translations");
  }

  @SimpleFunction(description = "Applies formatting to a specific layout.\n\n" +
          "Parameters:\n" +
          " - layout (component): The arrangement to apply formatting to.\n\n" +
          "How it works:\n" +
          "This block scans all text views within the provided layout. It looks for texts starting with '[' and ending with ']', containing three comma-separated values (e.g., [1,2,3]my text or [name,regular,label]).\n" +
          "These entries are then formatted using the active language and theme mode."
  )
  public void ApplyFormatting(AndroidViewComponent layout) {
    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    FormatTextViews(mScreenParent, ACTIVE_THEME_MODE, ACTIVE_TRANSLATION_LANGUAGE);
  }

  @SimpleFunction(description = "Applies formatting to a specific layout.\n\n" +
          "Parameters:\n" +
          " - layout (component): The arrangement to apply formatting to.\n" +
          " - themeMode (String): Specifies the theme mode for the color. Must be one of the predefined values from Modes\n" +
          " - languageCode (String): Specifies the translation language. Must be one of the predefined values from SupportedLanguages\n" +
          "How it works:\n" +
          "This block scans all text views within the provided layout. It looks for texts starting with '[' and ending with ']', containing three comma-separated values (e.g., [1,2,3]my text or [name,regular,label]).\n" +
          "These entries are then formatted using the themeMode and languageCode."
  )
  public void ApplyCustomizedFormatting(AndroidViewComponent layout, String themeMode, String languageCode) {
    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    FormatTextViews(mScreenParent, themeMode, languageCode);
  }

  @SimpleFunction(description = "Accesses keys or values from the preloaded dataset")
  public List<String> Get(@Options(All.class) String data){

    switch (data) {
      case "PrimitiveKeys":
        return new ArrayList<>(PRIMITIVE_COLORS.keySet());
      case "SemanticKeys":
        return new ArrayList<>(SEMANTIC_COLORS.get(ACTIVE_THEME_MODE).keySet());
      case "ThemeModes":
        return THEME_MODES;
      case "FontKeys":
        return new ArrayList<>(FONTS.keySet());
      case "TranslationKeys":
        return new ArrayList<>(translations.get(ACTIVE_TRANSLATION_LANGUAGE).keySet());
      default:
        return supportedLanguages;
    }


  }

  @SimpleFunction(description = "Retrieves the value for the given translationKey in the active translation language\n\n" +
          "Parameters:\n" +
          " - translationKey (String): The identifier for a specific translation entry\n\n" +
          "Returns:\n" +
          " - If a translation for the given translationKey is not found, returns 'Not Found'."
  )
  public String GetTranslation(String translationKey) {
    return GetTranslationForLanguage(translationKey, ACTIVE_TRANSLATION_LANGUAGE);
  }

  @SimpleFunction(description = "Retrieves the value for the specified translationKey in the given language code.\n\n" +
          "Parameters:\n" +
          " - translationKey (String): The identifier for a specific translation entry\n\n" +
          " - languageCode (String): Specifies the translation language. Must be one of the predefined values from SupportedLanguages\n" +
          "Returns:\n" +
          " - If a translation for the given translationKey is not found, returns 'Not Found'.\n" +
          " - If the provided languageCode is not in SupportedLanguages, returns 'languageCode is not supported'."
  )
  public String GetTranslationForLanguage(String translationKey, String languageCode) {

    if (!supportedLanguages.contains(languageCode)) {
      ErrorOccurred("GetTranslation", "Error: Language '" + languageCode + "' is not supported.");
      return languageCode + " is not supported.";
    }

    HashMap<String, String> langMap;

    if (Objects.equals(languageCode, ACTIVE_TRANSLATION_LANGUAGE)){
      langMap = ACTIVE_TRANSLATION_LANGUAGE_MAP;
    } else{
      langMap = translations.get(languageCode);
    }

    if (langMap != null && langMap.containsKey(translationKey)) {
      return langMap.get(translationKey);
    } else {
      ErrorOccurred("GetTranslationForLanguage", "Error: No translation found for key '" + translationKey + "'");
      return "Not Found";
    }

  }

  @SimpleFunction(description = "Returns the font associated with the given tag. If the tag is not found, logs an error and returns a default message.")
  public String GetFont(String fontName) {

    if (!FONTS.containsKey(fontName)) {
      ErrorOccurred("GetFont","Font '" + fontName + "' does not exist." );
      return "";
    }

    // Return the font value
    return FONTS.get(fontName);
  }



  @SimpleFunction(description = "This method retrieves the integer value of a primitive color for a given key from the Primitive Colors.\n" +
          "\n" +
          "Parameters:\n" +
          "\n" +
          " - key (String): The identifier for the desired primitive color.\n" +
          "Returns:\n" +
          "\n" +
          " - The resolved integer color value if the key exists in the Primitive Colors.\n" +
          " - '-1' if the key does not exist, indicating an error.")
  public int GetPrimitiveColor(String key) {
    // Check if the key exists in the primitiveColors map
    if (!PRIMITIVE_COLORS.containsKey(key)) {
      ErrorOccurred("GetPrimitiveColor","Primitive color key '" + key + "' does not exist." );
      return -1;
    }

    // Return the color value
    return PRIMITIVE_COLORS.get(key);
  }


  @SimpleFunction(description = "This method retrieves the source reference of a semantic color as a String for a given key in the currently active theme mode.\n" +
          "\n" +
          "Parameters:\n" +
          "\n" +
          " - key (String): The identifier for the desired semantic color.\n" +
          "Returns:\n" +
          "\n" +
          " - A String containing the source reference of the semantic color if found.\n" +
          " - A descriptive error message if the active theme mode or the key does not exist.")
  public String GetSemanticColorSource(String key) {
    String activeThemeMode = ACTIVE_THEME_MODE;
    // Check if the mode exists in the semantic map
    if (!SEMANTIC_COLORS_SOURCE.containsKey(activeThemeMode)) {
      ErrorOccurred("GetSemanticColor", "Error: Mode '" + activeThemeMode + "' does not exist.");
      return "Error: Mode '" + activeThemeMode + "' does not exist.";
    }

    // Get the mode map
    HashMap<String, String> activeColorModeMap = SEMANTIC_COLORS_SOURCE.get(activeThemeMode);

    // Check if the key exists in the mode map
    if (!activeColorModeMap.containsKey(key)) {
      ErrorOccurred("GetSemanticColor", "Error: Key '" + key + "' does not exist in mode '" + activeThemeMode + "'.");
      return "Error: Key '" + key + "' does not exist in mode '" + activeThemeMode + "'.";
    }

    // Return the color value
    return activeColorModeMap.get(key);
  }

  @SimpleFunction(description = "This method retrieves the resolved integer value of a semantic color from primitive colors for a given key in the currently active theme mode.\n" +
          "\n" +
          "Parameters:\n" +
          "\n" +
          " - key (String): The name or identifier of the semantic color to retrieve from primitive colors .\n" +
          "Returns:\n" +
          "\n" +
          " - The resolved color value as an int if the key exists in the active theme mode.\n" +
          " - '-1' if:\n" +
          "        - The active theme mode does not exist in the Semantic Colors.\n" +
          "        - The specified Semantic Color does not exist in the Theme Mode.")
  public int GetSemanticColor(String key) {
    String activeThemeMode = ACTIVE_THEME_MODE;
    // Check if the mode exists in the semantic map
    if (!SEMANTIC_COLORS.containsKey(activeThemeMode)) {
      ErrorOccurred("GetSemanticColor", "Error: Mode '" + activeThemeMode + "' does not exist.");
      return -1;
    }

    // Get the mode map
    HashMap<String, Integer> activeThemeModeMap = SEMANTIC_COLORS.get(activeThemeMode);

    // Check if the key exists in the mode map
    if (!activeThemeModeMap.containsKey(key)) {
      ErrorOccurred("GetSemanticColor", "Error: Key '" + key + "' does not exist in mode '" + activeThemeMode + "'.");
      return -1;
    }

    // Return the color value
    return activeThemeModeMap.get(key);
  }

  @SimpleFunction(description = "This method retrieves the resolved integer value of a semantic color from primitive colors for a given key and theme mode.\n" +
          "\n" +
          "Parameters:\n" +
          "\n" +
          " - key (String): The name or identifier of the semantic color to retrieve from primitive colors.\n" +
          " - themeMode (String): Specifies the theme mode for the color. Must be one of the predefined values from Modes\n" +
          "Returns:\n" +
          "\n" +
          " - The resolved color value as an int if the key exists in the active theme mode.\n" +
          " - '-1' if:\n" +
          "        - The theme mode does not exist in the Semantic Colors.\n" +
          "        - The specified Semantic Color does not exist in the Theme Mode."
  )
  public int GetSemanticColorByThemeMode(String key, String themeMode) {

    // Check if the mode exists in the semantic map
    if (!SEMANTIC_COLORS.containsKey(themeMode) || !THEME_MODES.contains(themeMode)) {
      ErrorOccurred("GetSemanticColorByThemeMode", "Error: Mode '" + themeMode + "' does not exist.");
      return -1;
    }

    HashMap<String, Integer> activeThemeModeMap;

    if (Objects.equals(themeMode, ACTIVE_THEME_MODE)){
      activeThemeModeMap = ACTIVE_THEME_MODE_COLOR_MAP;
    } else {
      activeThemeModeMap = SEMANTIC_COLORS.get(themeMode);
    }

    // Check if the key exists in the mode map
    if (!activeThemeModeMap.containsKey(key)) {
      ErrorOccurred("GetSemanticColor", "Error: Key '" + key + "' does not exist in mode '" + themeMode + "'.");
      return -1;
    }

    // Return the color value
    return activeThemeModeMap.get(key);
  }

  //  ========== VERSION 2.1 METHODS START ===========  //

  @SimpleFunction(description = "Initialize the extension\nIf you want bold/italic font to be same font as regular then set value r.")
  public void InitializeV2_1(YailDictionary colorScheme, YailDictionary fonts, String translationFiles, String defaultLanguage) {
    parseColorScheme(colorScheme);
    parseFonts(fonts);
    parseTranslationFiles(translationFiles);
    mTranslationLanguage = defaultLanguage;
  }

  @SimpleFunction(description = "Update the color scheme")
  public void UpdateColorScheme(YailDictionary colorScheme) {
    updateColorScheme(colorScheme);
  }

  @SimpleFunction(description = "Update the font styles")
  public void UpdateFonts(YailDictionary fonts) {
    updateFonts(fonts);
  }

  @SimpleFunction(description = "Translates all the textview")
  public void TranslateApp(AndroidViewComponent layout) {
    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    findTextViews(mScreenParent, mTranslationLanguage);
  }

  @SimpleFunction(description = "Translates all the textview")
  public void TranslateAppForLanguage(AndroidViewComponent layout, String language) {

    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    findTextViews(mScreenParent, language);
  }

  @SimpleFunction(description = "")
  public String GetString(String translationText) {
    return getTranslation(translationText,mTranslationLanguage);
  }



  @SimpleFunction(description = "")
  public String GetStringForLanguage(String translationText, String language) {
    return getTranslation(translationText,language);
  }

  @SimpleFunction(description = "")
  public int GetColor(String color) {
    if(colorMapV2.containsKey(color.substring(0, 1))){
      return colorMapV2.get(color.substring(0, 1));
    }else {
      ErrorOccurred("GetColor", "Color not found or invalid color");
      return 0;
    }

  }

  @SimpleFunction(description = "")
  public void UpdateLanguage(String language){
    if (translationMapV2.containsKey(language)){
      mTranslationLanguage = language;
    }else {
      ErrorOccurred("UpdateLanguage", "Translation file not found");
    }

  }
  @SimpleFunction(description = "")
  public String GetLanguage(){
    return mTranslationLanguage;
  }

  //  ========== VERSION 2.1 METHODS END ===========  //

  //---------------------------------------------------------------------------
  //Private Methods
  //---------------------------------------------------------------------------

  private void parseColors(String colors) {
    try {
      ThmltJsonConfigValidator.ValidationResult result = ThmltJsonConfigValidator.validateThmltJson(colors);

      PRIMITIVE_COLORS.clear();
      SEMANTIC_COLORS.clear();
      ACTIVE_THEME_MODE = "";
      THEME_MODES.clear();

      // --- 1. Extract Primitives ---
      JsonNode primitives = result.correctedJson.path("Primitives");
      Iterator<String> keys = primitives.fieldNames();
      while (keys.hasNext()) {
        String key = keys.next();
        String hex = primitives.path(key).asText();
        PRIMITIVE_COLORS.put(key, formatColor(hex));
      }

      // --- 2. Extract Semantic ---
      JsonNode semantic = result.correctedJson.path("Semantic");
      Iterator<String> modes = semantic.fieldNames();
      while (modes.hasNext()) {
        String mode = modes.next();
        JsonNode colorMap = semantic.path(mode);
        HashMap<String, Integer> modeColors = new HashMap<String, Integer>();
        HashMap<String, String> modeColorSources = new HashMap<String, String>();

        Iterator<String> colorKeys = colorMap.fieldNames();
        while (colorKeys.hasNext()) {
          String name = colorKeys.next();
          String ref = colorMap.path(name).asText();
          Integer colorValue = PRIMITIVE_COLORS.getOrDefault(ref, 0xFFFFFFFF);

          modeColorSources.put(name, ref);
          modeColors.put(name, colorValue);
        }
        THEME_MODES.add(mode);
        SEMANTIC_COLORS_SOURCE.put(mode, modeColorSources);
        SEMANTIC_COLORS.put(mode, modeColors);
      }

      ThemeMode(result.correctedJson.path("DefaultMode").asText());

      for (String item : result.errors) {
        ErrorOccurred("Initialize", item);
      }

      for (String item : result.warnings) {
        ErrorOccurred("Initialize", item);
      }

    } catch (IOException e) {
      ErrorOccurred("Initialize", String.valueOf(e));
    }
  }

  private void parseFonts(String fonts) {
    try {
      ThmltJsonConfigValidator.ValidationResult result = ThmltJsonConfigValidator.validateFontsJson(fonts);
      JsonNode fontsNode = result.correctedJson.path("Fonts");

      FONTS.clear();

      Iterator<String> fontKeys = fontsNode.fieldNames();
      while (fontKeys.hasNext()) {
        String fontKey = fontKeys.next();
        JsonNode fontValueNode = fontsNode.get(fontKey);

        if (fontValueNode.isTextual()) {
          String fontFile = fontValueNode.asText();
          FONTS.put(fontKey, fontFile);
        }
      }

    } catch (IOException e) {
      ErrorOccurred("ParseFontsJson", String.valueOf(e));
    }
  }

  public void parseTranslations(String translationsJson) {
    try {
      ThmltJsonConfigValidator.ValidationResult result = ThmltJsonConfigValidator.validateTranslationsJson(translationsJson);

      // Clear existing data
      supportedLanguages.clear();
      translations.clear();

      // Get corrected JSON
      ObjectNode corrected = result.correctedJson;
      ArrayNode langsArray = (ArrayNode) corrected.get("SupportedLanguages");

      // Populate supportedLanguages
      for (JsonNode langNode : langsArray) {
        supportedLanguages.add(langNode.asText());
      }

      // Fill translations map
      ObjectNode translationsNode = (ObjectNode) corrected.get("Translations");

      Iterator<String> keys = translationsNode.fieldNames();
      while (keys.hasNext()) {
        String translationKey = keys.next();
        ObjectNode langValues = (ObjectNode) translationsNode.get(translationKey);

        Iterator<String> langs = langValues.fieldNames();
        while (langs.hasNext()) {
          String langCode = langs.next();
          String value = langValues.get(langCode).asText();

          translations.putIfAbsent(langCode, new HashMap<>());
          translations.get(langCode).put(translationKey, value);
        }
      }

      Language(result.correctedJson.path("DefaultLanguage").asText());

    } catch (IOException e) {
        ErrorOccurred("Initialize", String.valueOf(e));
    }
  }

  private int formatColor(String colorValue) {
    try {
      if (colorValue.startsWith("#") && (colorValue.length() == 7 || colorValue.length() == 9)) {
        return android.graphics.Color.parseColor(colorValue);
      } else {
        return Integer.parseInt(colorValue);
      }
    } catch (IllegalArgumentException e) {
      return 0;
    }
  }

  public void FormatTextViews(View v, String themeMode, String languageCode) {
    try {
      if (v instanceof ViewGroup) {
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
          View child = vg.getChildAt(i);
          // recursively call this method
          FormatTextViews(child, themeMode, languageCode);
        }
      } else if (v instanceof TextView) {
        TextView textView = (TextView) v;
        String text = textView.getText().toString();

        // Regex to extract the array and the rest
        String regex = "^\\s*\\[\\s*([^,\\]]+?)\\s*,\\s*([^,\\]]+?)\\s*,\\s*([^\\]]+?)\\s*\\](.*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

          String mStrTranslate = matcher.group(1).trim();
          String mStrFont = matcher.group(2).trim();
          String mStrColor = matcher.group(3).trim();
          String remainingText  = matcher.group(4).trim();

          // Handle Translations
          if (mStrTranslate.equals("#")){
            textView.setText(remainingText);
          } else {
            textView.setText(GetTranslationForLanguage(mStrTranslate, languageCode));
          }

          // Handle font section
          if (!mStrFont.equals("#")) {
            String fontFileName = FONTS.get(mStrFont);

            if (fontFileName != null && !fontFileName.trim().isEmpty()) {
              try {
                Typeface typeface = null;

                if (this.IS_REPL) {
                  String basePath = Build.VERSION.SDK_INT > 28
                          ? "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/assets/"
                          : "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/AppInventor/assets/";
                  File fontFile = new File(basePath.concat(fontFileName));
                  if (fontFile.exists()) {
                    typeface = Typeface.createFromFile(fontFile);
                  } else {
                    ErrorOccurred("Formatting", "Font file not found: " + fontFile.getAbsolutePath());
                    Log.w(TAG, "Font file not found: " + fontFile.getAbsolutePath());
                  }
                } else {
                  typeface = Typeface.createFromAsset(textView.getContext().getAssets(), fontFileName);
                }

                if (typeface != null) {
                  textView.setTypeface(typeface);
                }

              } catch (Exception e) {
                ErrorOccurred("Formatting", "Failed to set font: " + fontFileName);
                Log.e(TAG, "Failed to set font: " + fontFileName, e);
                // Optional fallback: textView.setTypeface(Typeface.DEFAULT);
              }
            } else {
              Log.e(TAG, "Invalid Font Name");
              ErrorOccurred("Formatting", "Invalid Font Name");
            }
          }

          // Handle Color Section
          if (!mStrColor.equals("#")){

            // Check if the mode exists in the semantic map
            HashMap<String, Integer> themeModeMap =
                    Objects.equals(themeMode, ACTIVE_THEME_MODE)
                            ? ACTIVE_THEME_MODE_COLOR_MAP
                            : SEMANTIC_COLORS.get(themeMode);

            if (themeModeMap != null) {
              Integer color = themeModeMap.get(mStrColor);
              if (color != null) {
                textView.setTextColor(color);
              } else {
                ErrorOccurred("ApplyFormatting", "Error: Key '" + mStrColor + "' does not exist in mode '" + themeMode + "'.");
              }
            } else {
              ErrorOccurred("ApplyFormatting", "Error: Mode '" + themeMode + "' does not exist.");
            }

          }

        }
      }
    } catch (Exception e) {
      ErrorOccurred("FormatTextViews", String.valueOf(e));
    }
  }

  private String loadJsonFromAssets(Context context, String fileName) {
    try {
      if (this.IS_REPL) {
        String basePath = Build.VERSION.SDK_INT > 28
                ? "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/assets/"
                : "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/AppInventor/assets/";
        File jsonFile = new File(basePath.concat(fileName));
        if (jsonFile.exists()) {
          FileInputStream fis = new FileInputStream(jsonFile);
          return readStream(fis);
        } else {
          Log.e(TAG, "JSON file not found: " + jsonFile.getAbsolutePath());
          ErrorOccurred("LoadJson", "JSON file not found: " + jsonFile.getAbsolutePath());
          return null;
        }
      } else {
        InputStream is = context.getAssets().open(fileName);
        return readStream(is);
      }
    } catch (IOException e) {
      Log.e(TAG, "Failed to load JSON: " + fileName, e);
      ErrorOccurred("LoadJson", "Failed to load JSON: " + e.getMessage());
      return null;
    }
  }

  private String readStream(InputStream inputStream) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    return result.toString(StandardCharsets.UTF_8.name());
  }

  private void parseInitializationInput(String label, String input, String type) {
    try {
      String json;
      if (input.trim().endsWith(".json")) {
        json = loadJsonFromAssets(context, input.trim());
        if (json == null) {
          ErrorOccurred("Initialize", label + " file not found or unreadable.");
          return;
        }
      } else if (input.trim().startsWith("{") && input.trim().endsWith("}")) {
        json = input.trim();
      } else {
        ErrorOccurred("Initialize", label + " is not valid JSON or a .json file.");
        return;
      }

      if (type.equals("colorThemes")) {
        parseColors(json);
      } else if (type.equals("fonts")) {
        parseFonts(json);
      } else if (type.equals("translations")) {
        parseTranslations(json);
      }

    } catch (Exception e) {
      Log.e(TAG, "Error in Initialize: " + label, e);
      ErrorOccurred("Initialize", "Failed to initialize " + label + ": " + e.getMessage());
    }
  }

  //  ========== VERSION 2.1 PRIVATE METHODS START ===========  //

  private void updateColorScheme(YailDictionary colorScheme) {
    for (Object key : colorScheme.keySet()) {
      String colorKey = key.toString().substring(0, 1);
      int parsedColor = parseColor(colorScheme.get(key).toString());
      if (parsedColor == 0) {
        ErrorOccurred("colorScheme", key + ": " + colorScheme.get(key).toString() + " is not a valid color");
        continue; // Skip setting the color if it's not valid
      }
      switch (colorKey) {
        case "p":
          mColorPrimary = parsedColor;
          break;
        case "s":
          mColorSecondary = parsedColor;
          break;
        case "a":
          mColorAccent = parsedColor;
          break;
      }
      if (colorMapV2.containsKey(colorKey)) {
        colorMapV2.replace(colorKey, parsedColor);
      } else {
        colorMapV2.put(colorKey, parsedColor);
      }
    }
  }

  private void updateFonts(YailDictionary fonts) {
    for (Object key : fonts.keySet()) {
      String fontKey = key.toString().substring(0, 1);
      String fontValue = fonts.get(key).toString();
      switch (fontKey) {
        case "r":
          mFontRegular = fontValue;
          break;
        case "b":
          mFontBold = fontValue;
          break;
        case "i":
          mFontItalic = fontValue;
          break;
        case "m":
          mFontMaterial = fontValue;
          break;
      }
      if (fontMapV2.containsKey(fontKey)) {
        fontMapV2.replace(fontKey, fontValue);
      } else {
        fontMapV2.put(fontKey, fontValue);
      }
    }
  }

  private void parseColorScheme(YailDictionary colorScheme) {
    for (Object key : colorScheme.keySet()) {
      String colorKey = key.toString().substring(0, 1);
      String colorValue = colorScheme.get(key).toString();
      int parsedColor = parseColor(colorValue);
      if (parsedColor != 0) {
        switch (colorKey) {
          case "p":
            mColorPrimary = parsedColor;
            break;
          case "s":
            mColorSecondary = parsedColor;
            break;
          case "a":
            mColorAccent = parsedColor;
            break;
        }
        colorMapV2.put(colorKey, parsedColor);
      } else {
        ErrorOccurred("colorScheme", key + ": " + colorValue + " is not a valid color");
      }
    }
  }

  private void parseFonts(YailDictionary fonts) {
    for (Object key : fonts.keySet()) {
      String fontKey = key.toString().substring(0, 1);
      String fontValue = fonts.get(key).toString();
      switch (fontKey) {
        case "r":
          mFontRegular = fontValue;
          break;
        case "b":
          mFontBold = fontValue;
          break;
        case "i":
          mFontItalic = fontValue;
          break;
        case "m":
          mFontMaterial = fontValue;
          break;
      }
      fontMapV2.put(fontKey, fontValue);
    }
  }


  public void parseTranslationFiles(String jsonString) {
    try {
      JSONObject jsonObject = new JSONObject(jsonString);

      // Read the supported languages
      JSONArray supportedLangsArray = jsonObject.getJSONArray("SupportedLanguages");
      for (int i = 0; i < supportedLangsArray.length(); i++) {
        supportedLanguagesV2.add(supportedLangsArray.getString(i));
      }

      // Iterate through the translation keys
      JSONObject translationObject = jsonObject.getJSONObject("Translations");
      Iterator<String> textKeys = translationObject.keys();

      while (textKeys.hasNext()) {
        String textKey = textKeys.next();
        JSONObject langTranslations = translationObject.getJSONObject(textKey);

        HashMap<String, String> langMap = new HashMap<>();
        Iterator<String> languages = langTranslations.keys();

        // Store only supported language translations
        while (languages.hasNext()) {
          String language = languages.next();
          if (supportedLanguagesV2.contains(language)) {
            String value = langTranslations.optString(language, "Not Found");  // Use "Not Found" for missing keys
            langMap.put(language, value);
          } else {
            // Ignore unsupported language
            ErrorOccurred("parseTranslation", "Warning: Language '" + language + "' is not in SupportedLanguages. Ignoring...");
          }
        }

        translationsV2.put(textKey, langMap);
        Log.i(TAG, textKey);
        Log.i(TAG, String.valueOf(langMap));
      }

    } catch (JSONException e) {
      ErrorOccurred("parseTranslation", "Error parsing JSON: " + e.getMessage());
    }
  }

  // Method to get specific translation by text key and language
  public String getTranslation(String textKey, String language) {
    if (translationsV2.containsKey(textKey)) {
      HashMap<String, String> langMap = translationsV2.get(textKey);

      // Check if the language is supported
      if (supportedLanguagesV2.contains(language)) {
        return langMap.getOrDefault(language, "Not Found");  // Return "Not Found" if missing
      } else {
        ErrorOccurred("getTranslation","Error: Language '" + language + "' is not supported.");
        return language + " is not supported.";
      }
    } else {
      ErrorOccurred("getTranslation","Error: No translation found for key '" + textKey + "'");
      return "Not Found";
    }
  }

  private int parseColor(String colorValue) {
    try {
      if (colorValue.startsWith("#") && (colorValue.length() == 7 || colorValue.length() == 9)) {
        return android.graphics.Color.parseColor(colorValue);
      } else {
        return Integer.parseInt(colorValue);
      }
    } catch (IllegalArgumentException e) {
      return 0;
    }
  }

  public void findTextViews(View v, String lang) {
    try {
      if (v instanceof ViewGroup) {
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
          View child = vg.getChildAt(i);
          // recursively call this method
          findTextViews(child, lang);
        }
      } else if (v instanceof TextView) {
        //do whatever you want ...
        TextView textView = (TextView) v;
        String text = textView.getText().toString();
        String textToDisplay;

        if (text.startsWith("{-")) {
          String mStrTranslate = text.substring(2, 3); // Get the mStrTranslate letter
          String mStrFont = text.substring(3, 4); // Get the mStrFont letter
          String mStrColor = text.substring(4, 5); // Get the mStrColor letter

          // Handle translation
          if (mStrTranslate.equals("t")) {
            textToDisplay = GetStringForLanguage(text.substring(6), lang);
            textView.setText(textToDisplay);
          } else {
            textToDisplay = text.substring(6);
            textView.setText(textToDisplay);
          }

          // Handle font
          if (fontMapV2.containsKey(mStrFont)) {
            String font = fontMapV2.get(mStrFont);
            Typeface typeface;
            if (this.IS_REPL) {
              if (Build.VERSION.SDK_INT > 28) {
                typeface = Typeface.createFromFile(new java.io.File("/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/assets/".concat(String.valueOf(font))));
              } else {
                typeface = Typeface.createFromFile(new java.io.File("/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/AppInventor/assets/".concat(String.valueOf(font))));
              }
            } else {
              typeface = Typeface.createFromAsset(textView.getContext().getAssets(), font);
            }

            textView.setTypeface(typeface);
          }
          // Handle color
          if (colorMapV2.containsKey(mStrColor)) {
            textView.setTextColor(colorMapV2.get(mStrColor));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //  ========== VERSION 2.1 PRIVATE METHODS END ===========  //

}
