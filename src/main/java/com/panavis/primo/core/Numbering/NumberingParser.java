package com.panavis.primo.core.Numbering;

import com.panavis.primo.core.Numbering.Formats.DefaultFormat;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.*;

import static com.panavis.primo.core.Numbering.Constants.HEADING;
import static com.panavis.primo.core.Numbering.Constants.LIST_PARAGRAPH;

public class NumberingParser {

    private final XWPFNumbering numbering;
    private List<String> uniqueNumIds;
    private Map<String, Integer> uniqueNumberingFormatTracker;
    private Map<String, String> numberingFormatNames;
    private Map<String, String> numberingFormatDisplays;
    private Map<String, Integer> numberingCountStart;
    private Map<String, Integer> previousUnitNumbering;
    private Map<Integer, UnitNumbering> unitNumberings;

    public NumberingParser(XWPFNumbering numbering) {
        this.numbering = numbering;
        this.uniqueNumIds = new ArrayList<>();
        this.uniqueNumberingFormatTracker = new HashMap<>();
        this.numberingFormatNames = new HashMap<>();
        this.numberingFormatDisplays = new HashMap<>();
        this.numberingCountStart = new HashMap<>();
        this.previousUnitNumbering = new HashMap<>();
        this.unitNumberings = new HashMap<>();
    }

    public Map<Integer, UnitNumbering> getParagraphsNumbering(List<XWPFParagraph> paragraphs) {
        for (int i = 0; i < paragraphs.size(); i++) {
            XWPFParagraph paragraph = paragraphs.get(i);
            String paragraphStyle = paragraph.getStyle();
            if (paragraphHasNumbering(paragraph, paragraphStyle))
                identifyFormatAndAddToList(i, paragraph, paragraphStyle);
            else
                addDefaultUnitNumbering(i);
        }
        return unitNumberings;
    }

    public static boolean paragraphHasNumbering(XWPFParagraph paragraph, String paragraphStyle) {
        return paragraphStyle != null &&
                (paragraphStyle.equals(LIST_PARAGRAPH) ||
                        (paragraphStyle.startsWith(HEADING) && paragraph.getNumIlvl() != null));
    }

    private void identifyFormatAndAddToList(int i, XWPFParagraph paragraph, String paragraphStyle) {
        BigInteger numId = paragraph.getNumID();
        int levelValue = paragraph.getNumIlvl().intValue();
        String uniqueId = numId.toString() + "_" + levelValue;
        updateUniqueNumberingFormatTracker(uniqueId);
        if (!uniqueNumIds.contains(uniqueId)) {
            uniqueNumIds.add(uniqueId);
            saveNumIdProperties(paragraph, numId, uniqueId);
        }
        addUnitNumberingAndSetPreviousRealNext(i, uniqueId, paragraphStyle);
    }

    private void updateUniqueNumberingFormatTracker(String uniqueId) {
        if (this.uniqueNumberingFormatTracker.containsKey(uniqueId)) {
            int currentValue = this.uniqueNumberingFormatTracker.get(uniqueId);
            this.uniqueNumberingFormatTracker.put(uniqueId, currentValue + 1);
        }
        else {
            this.uniqueNumberingFormatTracker.put(uniqueId, 1);
        }
    }

    private void saveNumIdProperties(XWPFParagraph paragraph, BigInteger numId, String uniqueId) {
        CTLvl abstractNumLvl = this.getAbstractNumLvl(paragraph, numId);
        this.saveNumCountStart(uniqueId, abstractNumLvl);
        this.saveNumFormatName(uniqueId, abstractNumLvl);
        this.saveNumFormatDisplay(uniqueId, abstractNumLvl);
    }

    private CTLvl getAbstractNumLvl(XWPFParagraph paragraph, BigInteger numId) {
        BigInteger abstractNumIndex = this.numbering.getAbstractNumID(numId);
        XWPFAbstractNum abstractNum = this.numbering.getAbstractNum(abstractNumIndex);
        CTAbstractNum ctAbstractNum = abstractNum.getCTAbstractNum();
        int abstractNumLvlIndex = paragraph.getNumIlvl().intValue();
        return ctAbstractNum.getLvlArray(abstractNumLvlIndex);
    }

    private void saveNumCountStart(String uniqueId, CTLvl abstractNumLvl) {
        try {
            int numStart = abstractNumLvl.getStart().getVal().intValue();
            this.numberingCountStart.put(uniqueId, numStart);
        }
        catch (NullPointerException e) {
            this.numberingCountStart.put(uniqueId, 0);
        }
    }

    private void saveNumFormatName(String uniqueId, CTLvl abstractNumLvl) {
        String numFormat = abstractNumLvl.getNumFmt().getVal().toString();
        this.numberingFormatNames.put(uniqueId, numFormat);
    }

    private void saveNumFormatDisplay(String uniqueId, CTLvl abstractNumLvl) {
        String numDisplay = abstractNumLvl.getLvlText().getVal();
        this.numberingFormatDisplays.put(uniqueId, numDisplay);
    }

    private void addUnitNumberingAndSetPreviousRealNext(int paragraphIndex, String uniqueId, String style) {
        UnitNumbering unitNumbering = this.getUnitNumbering(uniqueId, style);
        unitNumbering.current  = unitNumbering.current + "\t";
        this.unitNumberings.put(paragraphIndex, unitNumbering);
        if (this.previousUnitNumbering.containsKey(uniqueId)) {
            setPreviousNumberingRealNext(uniqueId, unitNumbering);
        }
        this.previousUnitNumbering.put(uniqueId, paragraphIndex);
    }

    private void setPreviousNumberingRealNext(String uniqueId, UnitNumbering unitNumbering) {
        int previousParagraph = this.previousUnitNumbering.get(uniqueId);
        UnitNumbering previousNumbering = this.unitNumberings.get(previousParagraph);
        previousNumbering.setRealNext(unitNumbering.current);
    }

    private UnitNumbering getUnitNumbering(String uniqueId, String style) {
        String formatName = this.numberingFormatNames.get(uniqueId);
        String formatDisplay = this.numberingFormatDisplays.get(uniqueId);
        int currentNumber = this.uniqueNumberingFormatTracker.get(uniqueId) +
                this.numberingCountStart.get(uniqueId) - 1;
        return UnitNumbering.builder(formatName, formatDisplay)
                                        .setCurrentNumbering(currentNumber)
                                        .setLogicalNextNumbering(currentNumber)
                                        .setNumberingStyle(style);
    }

    private void addDefaultUnitNumbering(int paragraphIndex) {
        this.unitNumberings.put(paragraphIndex, new DefaultFormat());
    }
}