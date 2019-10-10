package com.panavis.primo.core.Numbering.Formats;

import com.panavis.primo.core.Numbering.UnitNumbering;

public class Bullet extends UnitNumbering {

    private final String BULLET = "-";

    public Bullet(String formatDisplay) {
        super(formatDisplay);
    }

    @Override
    public UnitNumbering setCurrentNumbering(int number) {
        this.current = BULLET;
        return this;
    }

    @Override
    public UnitNumbering setLogicalNextNumbering(int number) {
        this.logicalNext = BULLET;
        return this;
    }

    @Override
    public UnitNumbering setNumberingStyle(String style) {
        this.style = style;
        return this;
    }
}
