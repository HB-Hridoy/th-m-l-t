package com.hridoy.thmlt.utility;

public class TypographyTemplate {
    private final int fontSize;
    private final int lineHeight;
    private final float letterSpacing;
    private final String linkedFont;

    public TypographyTemplate(int fontSize, int lineHeight, float letterSpacing, String linkedFont) {
        this.fontSize = fontSize;
        this.lineHeight = lineHeight;
        this.letterSpacing = letterSpacing;
        this.linkedFont = linkedFont;
    }

    public int getFontSize() { return fontSize; }
    public int getLineHeight() { return lineHeight; }
    public float getLetterSpacing() { return letterSpacing; }
    public String getLinkedFont() { return linkedFont; }
}


