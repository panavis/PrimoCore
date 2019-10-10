package com.panavis.primo.core.Numbering.Formats;

import com.panavis.primo.core.Numbering.UnitNumbering;

public class Decimal extends UnitNumbering {

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
