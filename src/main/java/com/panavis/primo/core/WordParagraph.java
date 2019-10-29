package com.panavis.primo.core;

import com.panavis.primo.core.Numbering.UnitNumbering;

import java.util.*;

import static com.panavis.primo.core.Numbering.Constants.*;

public abstract class WordParagraph {

    private final List<ParagraphWrapper> paragraphWrappers;
    private final Map<Integer, Integer> postParagraphBlanks;
    private final Map<Integer, Boolean> numberedParagraphs;
    private final Map<Integer, UnitNumbering> unitNumberings;
    private final List<String> paragraphTexts;
    private final List<String> paragraphTextsWithoutNumbering;

    public WordParagraph(String wordFilePath) {
        WordPreprocessor wordPreprocessor = new WordPreprocessor(wordFilePath);
        this.paragraphWrappers = wordPreprocessor.getNonEmptyParagraphs();
        this.postParagraphBlanks = wordPreprocessor.getPostParagraphBlanks();
        this.numberedParagraphs = wordPreprocessor.getNumberedParagraphs();
        this.unitNumberings = wordPreprocessor.getUnitNumberings();
        this.paragraphTexts = wordPreprocessor.getParagraphTexts();
        this.paragraphTextsWithoutNumbering = wordPreprocessor.getParagraphTextsWithoutNumbering();
    }

    public abstract boolean isSectionHeading(int paragraphIndex);

    public ParagraphWrapper getParagraph(int paragraphIndex) {
        return paragraphWrappers.get(paragraphIndex);
    }

    public boolean paragraphExists(int index) {
        return index >= 0 && index < getNumberOfParagraphs();
    }

    public String getParagraphText(int paragraphIndex) {
        return paragraphTexts.get(paragraphIndex);
    }

    public String getParagraphTextWithoutNumbering(int paragraphIndex) {
        return paragraphTextsWithoutNumbering.get(paragraphIndex);
    }

    public UnitNumbering getUnitNumbering(int paragraphIndex) {
        return unitNumberings.get(paragraphIndex);
    }

    public int getNumberOfParagraphs() {
        return paragraphWrappers.size();
    }

    public boolean hasHeadingWithNumbering(int paragraphIndex) {
        UnitNumbering unitNumbering = unitNumberings.get(paragraphIndex);
        return numberedParagraphs.get(paragraphIndex) &&
                unitNumbering.style.startsWith(HEADING);
    }

    public String getBlankLinesAfterParagraph(int paragraphIndex) {
        int blanks = getNumberOfPostParagraphBlanks(paragraphIndex);
        return duplicateLineSeparator(blanks);
    }

    private static String duplicateLineSeparator(int numberOfLines) {
        return new String(new char[numberOfLines]).replace("\0", LINE_SEPARATOR);
    }

    public int getNumberOfPostParagraphBlanks(int paragraphIndex) {
        return postParagraphBlanks.get(paragraphIndex);
    }


    public String getParagraphFirstWord(int paragraphIndex) {
        String paragraphText = getParagraphText(paragraphIndex);
        return paragraphText.split(" ")[0];
    }

    public boolean isFirstRunBold(int paragraphIndex) {
        return isFirstRunBold(getParagraph(paragraphIndex));
    }

    public static boolean hasOneRun(ParagraphWrapper paragraphWrapper) {
        return paragraphWrapper.getParagraph().getRuns().size() == 1;
    }

    public static RunWrapper getNthRun(ParagraphWrapper paragraphWrapper, int runIndex) {
        List<RunWrapper> paragraphRuns = paragraphWrapper.getRuns();
        return paragraphRuns.get(runIndex);
    }

    public static String getNthRunText(ParagraphWrapper paragraphWrapper, int runIndex) {
        List<RunWrapper> runs = paragraphWrapper.getRuns();
        int numberOfRuns = runs.size();
        String nthRunText = "";
        if (numberOfRuns > runIndex)
            nthRunText = WordParagraph.getNthRun(paragraphWrapper, runIndex)
                    .getRun()
                    .text()
                    .trim();
        return nthRunText;
    }

    public static boolean isRunUnderlined(RunWrapper runWrapper) {
        int underlinePattern = runWrapper.getRun().getUnderline().getValue();
        return underlinePattern == 1 || underlinePattern == 4;
    }

    public static boolean isFirstRunBold(ParagraphWrapper paragraphWrapper) {
        RunWrapper firstRun = WordParagraph.getNthRun(paragraphWrapper, 0);
        boolean hasHeadingStyle = false;
        if (paragraphWrapper.getParagraph().getStyle() != null)
            hasHeadingStyle = paragraphWrapper.getParagraph().getStyle().equals("Heading1");
        String text = firstRun.getText();
        boolean isCaseSensitive = !text.toLowerCase().equals(text.toUpperCase());
        return (firstRun.getRun().isBold() || hasHeadingStyle) && isCaseSensitive;
    }

    public boolean isListedParagraph(int paragraphIndex) {
        String style = getUnitNumbering(paragraphIndex).style;
        return style.equals(LIST_PARAGRAPH);
    }
}
