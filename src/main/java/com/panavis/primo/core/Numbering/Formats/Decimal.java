package com.panavis.primo.core.Numbering.Formats;

import com.panavis.primo.core.Numbering.UnitNumbering;

import java.util.Map;

public class Decimal extends UnitNumbering {

    public static final Map<String, Integer> FIRST_DECIMAL_NUMBERS = Map.ofEntries(
            Map.entry("1", 1),
            Map.entry("2", 2),
            Map.entry("3", 3),
            Map.entry("4", 4),
            Map.entry("5", 5),
            Map.entry("6", 6),
            Map.entry("7", 7),
            Map.entry("8", 8),
            Map.entry("9", 9),
            Map.entry("10", 10)
    );

    public Decimal(String formatDisplay) {
        super(formatDisplay);
    }

    @Override
    public UnitNumbering setCurrentNumbering(int number) {
        this.current = getNumbering(number);
        return this;
    }

    @Override
    public UnitNumbering setLogicalNextNumbering(int number) {
        this.logicalNext = getNumbering(number + 1);
        return this;
    }

    private String getNumbering(int number) {
        String digit = String.valueOf(number);
        return formatDisplay.replaceAll("%\\d+", digit);
    }

    @Override
    public UnitNumbering setNumberingStyle(String style) {
        this.style = style;
        return this;
    }
}
