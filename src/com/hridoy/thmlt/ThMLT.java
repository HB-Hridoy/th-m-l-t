package com.hridoy.thmlt;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.YailDictionary;
import com.hridoy.thmlt.utility.ThmltJsonConfigValidator;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DesignerComponent(
	version = 21,
	versionName = "3",
	description = "Extension component for ThMLT. Created using FAST CLI.",
	iconName = "icon.png"
)
public class ThMLT extends AndroidNonvisibleComponent {


  private static final String TAG = "ThMLT";
  private static String mFontRegular = "";
  private static String mFontBold = "";
  private static String mFontItalic = "";
  private static String mFontMaterial = "";

  private static int mColorPrimary = 0;
  private static int mColorSecondary = 0;
  private static int mColorAccent = 0;


  private static String ACTIVE_TRANSLATION_LANGUAGE = "";


  private Typeface fontTypeface;

  private static HashMap<String, Integer> colorMap = new HashMap<>();

  private static String LOADED_VERSION = "2.1";
  private static List<String> THEME_MODES = new ArrayList<>();
  private static String ACTIVE_THEME_MODE = "";

  private static HashMap<String, Integer> PRIMITIVE_COLORS = new HashMap<>();
  private static HashMap<String, HashMap<String, String>> SEMANTIC_COLORS_SOURCE = new HashMap<>();
  private static HashMap<String, HashMap<String, Integer>> SEMANTIC_COLORS = new HashMap<>();

  private static HashMap<String, String> fontsByTag = new HashMap<>();
  private static HashMap<String, String> fontsByShortTag = new HashMap<>();

  private static List<String> supportedLanguages = new ArrayList<>();
  private static HashMap<String, HashMap<String, String>> translations = new HashMap<>();


  private static HashMap<String, JSONObject> translationMap = new HashMap<>();

  private HashMap<TextView, String> FORMATABLE_TEXT_VIEWS = new HashMap<>();
  private HashMap<TextView, String> FORMATABLE_TEXT_VIEWS_TRANSLATION = new HashMap<>();
  private HashMap<TextView, String> FORMATABLE_TEXT_VIEWS_FONT = new HashMap<>();
  private HashMap<TextView, String> FORMATABLE_TEXT_VIEWS_COLOR = new HashMap<>();


  private boolean isRepl = false;
  private final Context context;

  public ThMLT(ComponentContainer container) {
    super(container.$form());
    this.context = container.$context();
    if (this.form instanceof ReplForm) {
      this.isRepl = true;
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

  @SimpleProperty(description = "The italic font style")
  public String FontItalic() {
    return mFontItalic;
  }
  @SimpleProperty(description = "The italic font style")
  public void FontItalic(String fontItalic) {
    mFontItalic = fontItalic;
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

  @SimpleFunction(description = "Initialize the extension\nIf you want bold/italic font to be same font as regular then set value r.")
  public void Initialize(String colorThemes, YailDictionary fonts, String translations) {
    parseColors(colorThemes);
    parseFonts(fonts);
    parseTranslationFiles(translations);
  }

  @SimpleFunction(description = "Update the color scheme")
  public void UpdateColorScheme(YailDictionary colorScheme) {
    updateColorScheme(colorScheme);
  }

  @SimpleFunction(description = "Translates all the textview")
  public void TranslateApp(AndroidViewComponent layout) {
    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    ErrorOccurred("TranslateApp","Translating " + LOADED_VERSION);
    if (LOADED_VERSION.equals("2.1")){

      findTextViews(mScreenParent, ACTIVE_TRANSLATION_LANGUAGE);
    } else if (LOADED_VERSION.equals("3")) {
      FormatTextViews(mScreenParent, ACTIVE_TRANSLATION_LANGUAGE);
    }

  }

  @SimpleFunction(description = "Translates all the textview")
  public void TranslateAppForLanguage(AndroidViewComponent layout, String language) {

    ViewGroup mScreenParent = (ViewGroup) layout.getView();
    findTextViews(mScreenParent, language);
  }

  @SimpleFunction(description = "")
  public String GetString(String translationText) {
    return getTranslation(translationText, ACTIVE_TRANSLATION_LANGUAGE);
  }



  @SimpleFunction(description = "")
  public String GetStringForLanguage(String translationText, String language) {
   return getTranslation(translationText,language);
  }

  @SimpleFunction(description = "")
  public int GetColor(String color) {
    if(colorMap.containsKey(color.substring(0, 1))){
      return colorMap.get(color.substring(0, 1));
    }else {
      ErrorOccurred("GetColor", "Color not found or invalid color");
      return 0;
    }

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


  // Method to get color by mode and key
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

  // Method to get color by mode and key
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

  @SimpleFunction(description = "")
  public void ChangeThemeMode (String themeMode){
    if (THEME_MODES.contains(themeMode)){
      ACTIVE_THEME_MODE = themeMode;

      // Format TextViews
      for (Map.Entry<TextView, String> entry : FORMATABLE_TEXT_VIEWS_COLOR.entrySet()) {
        TextView textView = entry.getKey();   // Get the TextView (key)
        String textColor = entry.getValue(); // Get the associated String value

        /*if (textView != null) {

        } else {
          ErrorOccurred("ChangeThemeMode", FORMATABLE_TEXT_VIEWS.get(textView) + " not found");
        }*/
        textView.setTextColor(GetSemanticColor(textColor));
      }
    } else {
      ErrorOccurred("ChangeThemeMode", "Theme Mode doesn't exist");
    }
  }

  @SimpleFunction(description = "")
  public void UpdateLanguage(String language){
    if (translationMap.containsKey(language)){
      ACTIVE_TRANSLATION_LANGUAGE = language;
    }else {
      ErrorOccurred("UpdateLanguage", "Translation file not found");
    }

  }
  @SimpleFunction(description = "")
  public String GetLanguage(){
    return ACTIVE_TRANSLATION_LANGUAGE;
  }

  //---------------------------------------------------------------------------
  //Private Methods
  //---------------------------------------------------------------------------



  private void updateColorScheme(YailDictionary colorScheme) {
    for (Object key : colorScheme.keySet()) {
      String colorKey = key.toString().substring(0, 1);
      int parsedColor = formatColor(colorScheme.get(key).toString());
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
      if (colorMap.containsKey(colorKey)) {
        colorMap.replace(colorKey, parsedColor);
      } else {
        colorMap.put(colorKey, parsedColor);
      }
    }
  }

  private void parseColors(String colors) {
    try {
      ThmltJsonConfigValidator.ValidationResult result = ThmltJsonConfigValidator.validateThmltJson(colors);

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

        Iterator<String> colorKeys = colorMap.fieldNames();
        while (colorKeys.hasNext()) {
          String name = colorKeys.next();
          String ref = colorMap.path(name).asText();
          Integer colorValue = PRIMITIVE_COLORS.containsKey(ref)
                  ? PRIMITIVE_COLORS.get(ref)
                  : 0xFFFFFFFF; // fallback white
          modeColors.put(name, colorValue);
        }

        SEMANTIC_COLORS.put(mode, modeColors);
      }

      ACTIVE_THEME_MODE = result.correctedJson.path("DefaultMode").asText();

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

      fontsByTag.clear();
      fontsByShortTag.clear();

      Iterator<String> fontKeys = fontsNode.fieldNames();
      while (fontKeys.hasNext()) {
        String fontKey = fontKeys.next();
        JsonNode fontObj = fontsNode.get(fontKey);

        if (fontObj.isObject()) {
          String fontName = fontObj.path("fontName").asText();
          String shortTag = fontObj.path("shortFontTag").asText();

          fontsByTag.put(fontKey, fontName);
          fontsByShortTag.put(shortTag, fontName);
        }
      }

    } catch (IOException e) {
      ErrorOccurred("ParseFontsJson", String.valueOf(e));
    }
  }


  public void parseTranslationFiles(String jsonString) {
    try {
      JSONObject jsonObject = new JSONObject(jsonString);

      // Read the supported languages
      JSONArray supportedLangsArray = jsonObject.getJSONArray("SupportedLanguages");
      for (int i = 0; i < supportedLangsArray.length(); i++) {
        supportedLanguages.add(supportedLangsArray.getString(i));
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
          if (supportedLanguages.contains(language)) {
            String value = langTranslations.optString(language, "Not Found");  // Use "Not Found" for missing keys
            langMap.put(language, value);
          } else {
            // Ignore unsupported language
            ErrorOccurred("parseTranslation", "Warning: Language '" + language + "' is not in SupportedLanguages. Ignoring...");
          }
        }

        translations.put(textKey, langMap);
        Log.i(TAG, textKey);
        Log.i(TAG, String.valueOf(langMap));
      }

    } catch (JSONException e) {
      ErrorOccurred("parseTranslation", "Error parsing JSON: " + e.getMessage());
    }
  }

  // Method to get specific translation by text key and language
  public String getTranslation(String textKey, String language) {
    if (translations.containsKey(textKey)) {
      HashMap<String, String> langMap = translations.get(textKey);

      // Check if the language is supported
      if (supportedLanguages.contains(language)) {
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

  public void FormatTextViews(View v, String lang) {
    try {
      if (v instanceof ViewGroup) {
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
          View child = vg.getChildAt(i);
          // recursively call this method
          FormatTextViews(child, lang);
        }
      } else if (v instanceof TextView textView) {
        String text = textView.getText().toString();
        String textToDisplay;

        // Regex to extract the array and the rest
        String regex = "^\\s*\\[(\"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\")\\](.*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
          String mStrTranslate = matcher.group(2);
          String mStrFont = matcher.group(3);
          String mStrColor = matcher.group(4);
          String remainingText = matcher.group(5).trim();

          // Handle font section
          String fontName = null;
          if (!mStrFont.equals("#")) {
            fontName = fontsByTag.getOrDefault(mStrFont, fontsByShortTag.get(mStrFont));
            if (fontName != null){
              Typeface typeface;
              if (this.isRepl) {
                if (Build.VERSION.SDK_INT > 28) {
                  typeface = Typeface.createFromFile(new java.io.File("/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/assets/".concat(fontName)));
                } else {
                  typeface = Typeface.createFromFile(new java.io.File("/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/AppInventor/assets/".concat(fontName)));
                }
              } else {
                typeface = Typeface.createFromAsset(textView.getContext().getAssets(), fontName);
              }

              textView.setTypeface(typeface);
            }
          }

          // Handle Color Section
          textView.setTextColor(GetSemanticColor(mStrColor));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }




}
