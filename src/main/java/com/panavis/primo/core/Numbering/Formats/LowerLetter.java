package com.panavis.primo.core.Numbering.Formats;

import com.panavis.primo.core.Numbering.UnitNumbering;

public class LowerLetter extends UnitNumbering {

    public LowerLetter(String formatDisplay) {
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
        String currentLetter = getLowerLetter(number);
        return getFormattedString(formatDisplay, currentLetter);
    }

    private String getLowerLetter(int code) {
        return Character.toString((char) (96 + code));
    }

    @Override
    public UnitNumbering setNumberingStyle(String style) {
        this.style = style;
        return this;
    }
}
