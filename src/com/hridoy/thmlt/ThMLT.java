package com.hridoy.thmlt;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.YailDictionary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ThMLT extends AndroidNonvisibleComponent {


  private static final String TAG = "ThMLT";
  private static String mFontRegular = "";
  private static String mFontBold = "";
  private static String mFontItalic = "";
  private static String mFontMaterial = "";

  private static int mColorPrimary = 0;
  private static int mColorSecondary = 0;
  private static int mColorAccent = 0;


  private static String mTranslationLanguage = "";


  private Typeface fontTypeface;

  private static HashMap<String, Integer> colorMap = new HashMap<>();
  private static HashMap<String, String> fontMap = new HashMap<>();

  private static List<String> supportedLanguages = new ArrayList<>();
  private static HashMap<String, HashMap<String, String>> translations = new HashMap<>();


  private static HashMap<String, JSONObject> translationMap = new HashMap<>();


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
  public void Initialize(YailDictionary colorScheme, YailDictionary fonts, String translationFiles, String defaultLanguage) {
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
    if(colorMap.containsKey(color.substring(0, 1))){
      return colorMap.get(color.substring(0, 1));
    }else {
      ErrorOccurred("GetColor", "Color not found or invalid color");
      return 0;
    }

  }

  @SimpleFunction(description = "")
  public void UpdateLanguage(String language){
    if (translationMap.containsKey(language)){
      mTranslationLanguage = language;
    }else {
      ErrorOccurred("UpdateLanguage", "Translation file not found");
    }

  }
  @SimpleFunction(description = "")
  public String GetLanguage(){
    return mTranslationLanguage;
  }

  //---------------------------------------------------------------------------
  //Private Methods
  //---------------------------------------------------------------------------



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
      if (colorMap.containsKey(colorKey)) {
        colorMap.replace(colorKey, parsedColor);
      } else {
        colorMap.put(colorKey, parsedColor);
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
      if (fontMap.containsKey(fontKey)) {
        fontMap.replace(fontKey, fontValue);
      } else {
        fontMap.put(fontKey, fontValue);
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
        colorMap.put(colorKey, parsedColor);
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
      fontMap.put(fontKey, fontValue);
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
          if (fontMap.containsKey(mStrFont)) {
            String font = fontMap.get(mStrFont);
            Typeface typeface;
            if (this.isRepl) {
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
          if (colorMap.containsKey(mStrColor)) {
            textView.setTextColor(colorMap.get(mStrColor));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
