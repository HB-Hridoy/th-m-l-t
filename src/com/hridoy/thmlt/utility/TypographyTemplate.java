package com.hridoy.thmlt.utility;

public class TypographyTemplate {
    private final int fontSize;
    private final int lineHeight;
    private final int letterSpacing;
    private final String linkedFont;

    public TypographyTemplate(int fontSize, int lineHeight, int letterSpacing, String linkedFont) {
        this.fontSize = fontSize;
        this.lineHeight = lineHeight;
        this.letterSpacing = letterSpacing;
        this.linkedFont = linkedFont;
    }

    public int getFontSize() { return fontSize; }
    public int getLineHeight() { return lineHeight; }
    public int getLetterSpacing() { return letterSpacing; }
    public String getLinkedFont() { return linkedFont; }
}


