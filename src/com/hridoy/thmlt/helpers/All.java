package com.hridoy.thmlt.helpers;
import com.google.appinventor.components.common.OptionList;

import java.util.HashMap;
import java.util.Map;

public enum All implements OptionList<String> {
    PrimitiveKeys("PrimitiveKeys"),
    SemanticKeys("SemanticKeys"),
    ThemeModes("ThemeModes"),
    FontTags("FontTags"),
    FontShortTags("FontShortTags"),
    TranslationKeys("TranslationKeys"),
    SupportedLanguages("SupportedLanguages");

    private String all;

    All(String mAll) {
        this.all = mAll;
    }

    public String toUnderlyingValue() {
        return all;
    }

    private static final Map<String, All> lookup = new HashMap<>();

    static {
        for(All mAll : All.values()) {
            lookup.put(mAll.toUnderlyingValue(), mAll);
        }
    }

    public static All fromUnderlyingValue(String mAll) {
        return lookup.get(mAll);
    }
}

