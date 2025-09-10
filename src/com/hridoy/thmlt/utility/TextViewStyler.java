package com.hridoy.thmlt.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

import static com.hridoy.thmlt.ThMLT.TAG;

public class TextViewStyler {
    private final TextView textView;
    private final Context context;

    public TextViewStyler(TextView textView) {
        this.textView = textView;
        this.context = textView.getContext();
    }

    public static TextViewStyler with(TextView textView) {
        LogMessage.d("Styling TextView: "+textView);
        return new TextViewStyler(textView);
    }

    public TextViewStyler setText(String text) {
        LogMessage.d("Text: "+text);
        textView.setText(text);
        return this;
    }

    public TextViewStyler setTextSize(float sizeSp) {
        if (textView == null || sizeSp <= 0f) return this;

        LogMessage.d("Text Size: "+sizeSp);
        textView.setTextSize(sizeSp);
        return this;
    }

    public TextViewStyler setFont(String fontFileName, boolean isRepl) {
        try {
            Typeface typeface = null;

            if (isRepl) {
                String basePath = Build.VERSION.SDK_INT > 28
                        ? "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/assets/"
                        : "/storage/emulated/0/Android/data/edu.mit.appinventor.aicompanion3/files/AppInventor/assets/";
                File fontFile = new File(basePath.concat(fontFileName));
                if (fontFile.exists()) {
                    typeface = Typeface.createFromFile(fontFile);
                } else {
                    Log.w(TAG, "Font file not found: " + fontFile.getAbsolutePath());
                }
            } else {
                typeface = Typeface.createFromAsset(textView.getContext().getAssets(), fontFileName);
            }

            if (typeface != null) {
                LogMessage.d("Typeface: "+typeface);
                textView.setTypeface(typeface);
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to set font: " + fontFileName, e);
        }
        return this;
    }

    public TextViewStyler setTextColor(int color) {
        LogMessage.d("Text Color: "+color);
        textView.setTextColor(color);
        return this;
    }

    public TextViewStyler setLineHeight(int lineHeightPx) {
        if (textView == null || lineHeightPx <= 0) return this;

        LogMessage.d("Line Height: "+lineHeightPx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            textView.setLineHeight(lineHeightPx);
        } else {
            float fontSpacing = textView.getPaint().getFontSpacing();
            float extraSpacing = lineHeightPx - fontSpacing;
            textView.setLineSpacing(extraSpacing, 1f); // Multiplier = 1 to maintain existing scale
        }
        return this;
    }

    public TextViewStyler setLetterSpacing(float letterSpacingEm) {
        if (textView == null) return this;

        LogMessage.d("Letter Spacing: "+letterSpacingEm);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setLetterSpacing(letterSpacingEm);
        }
        return this;
    }


    public TextView apply() {
        LogMessage.d("Styling Applied Successfully");
        return textView;
    }
}

