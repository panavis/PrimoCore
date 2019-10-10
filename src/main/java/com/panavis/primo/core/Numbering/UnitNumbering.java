package com.panavis.primo.core.Numbering;

import com.panavis.primo.core.Numbering.Formats.*;

public abstract class UnitNumbering {

    public String current;
    public String realNext;
    public String logicalNext;
    public String style;
    protected String formatDisplay;

    public UnitNumbering(String formatDisplay) {
        this.formatDisplay = formatDisplay;
        this.realNext = "";
    }

    public abstract UnitNumbering setCurrentNumbering(int number);
    public abstract UnitNumbering setLogicalNextNumbering(int number);
    public abstract UnitNumbering setNumberingStyle(String style);

    public static UnitNumbering builder(String format, String display) {
        switch (format) {
            case "decimal": return new Decimal(display);
            case "lowerLetter": return new LowerLetter(display);
            case "upperLetter": return new UpperLetter(display);
            case "lowerRoman": return new LowerRoman(display);
            case "upperRoman": return new UpperRoman(display);
            case "bullet": return new Bullet(display);
            default: return new DefaultFormat();
        }
    }

    void setRealNext(String numbering) {
        this.realNext = numbering;
    }

    protected String getFormattedString(String template, String letter) {
        return template.replaceAll("%\\d", letter);
    }
}
