package com.panavis.primo.core.Numbering.Formats;

import com.panavis.primo.core.Numbering.RomanNumber;
import com.panavis.primo.core.Numbering.UnitNumbering;

import java.util.Map;

public class UpperRoman extends UnitNumbering {

    public static final Map<String, Integer> FIRST_ROMAN_NUMBERS = Map.ofEntries(
            Map.entry("I", 1),
            Map.entry("II", 2),
            Map.entry("III", 3),
            Map.entry("IV", 4),
            Map.entry("V", 5),
            Map.entry("VI", 6),
            Map.entry("VII", 7),
            Map.entry("VIII", 8),
            Map.entry("IX", 9),
            Map.entry("X", 10)
    );

    public UpperRoman(String formatDisplay) {
        super(formatDisplay);
    }

    @Override
    public UnitNumbering setCurrentNumbering(int number) {
        this.current = getNumbering(number);
        return this;
    }

    private String getNumbering(int number) {
        String currentLetter = RomanNumber.toRoman(number);
        return getFormattedString(formatDisplay, currentLetter);
    }

    @Override
    public UnitNumbering setLogicalNextNumbering(int number) {
        this.logicalNext = getNumbering(number + 1);
        return this;
    }

    @Override
    public UnitNumbering setNumberingStyle(String style) {
        this.style = style;
        return this;
    }
}
