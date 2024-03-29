package com.panavis.primo.core;

import com.panavis.primo.core.Numbering.NumberingParser;
import com.panavis.primo.core.Numbering.UnitNumbering;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WordPreprocessor {

    private final XWPFDocument wordDocument;
    private final List<ParagraphWrapper> paragraphWrappers;
    private final Map<Integer, Boolean> numberedParagraphs;
    private final Map<Integer, UnitNumbering> unitNumberings;

    public WordPreprocessor(String wordFilePath) {

        wordDocument = createWordDocumentObject(wordFilePath);
        paragraphWrappers = getNonEmptyParagraphs(wordDocument);
        numberedParagraphs = getNumberedParagraphs(paragraphWrappers);
        unitNumberings = getUnitNumberings(wordDocument, paragraphWrappers);
    }

    private static XWPFDocument createWordDocumentObject(String wordFilePath) {
        XWPFDocument wordDoc = null;
        try {
            wordDoc = new XWPFDocument(OPCPackage.open(wordFilePath));
            wordDoc.close();
        }
        catch (IOException |
                EmptyFileException |
                InvalidFormatException |
                NotOfficeXmlFileException e) {

            e.printStackTrace();
            System.out.println("File path: " + wordFilePath);
        }
        return wordDoc;
    }

    public static List<ParagraphWrapper> getNonEmptyParagraphs(XWPFDocument wordDocument) {
        List<ParagraphWrapper> bodyElements = getRawParagraphs(wordDocument);
        return bodyElements.stream()
                .filter(WordPreprocessor::paragraphHasContent)
                .collect(Collectors.toList());
    }

    public List<ParagraphWrapper> getNonEmptyParagraphs() {
        return isWordDocumentEmpty() ? new ArrayList<>() : paragraphWrappers;
    }

    private static List<ParagraphWrapper> getRawParagraphs(XWPFDocument wordDocument) {
        List<ParagraphWrapper> bodyElements = new ArrayList<>();

        if (wordDocument == null) return bodyElements;

        for (int i = 0; i < wordDocument.getBodyElements().size(); i++) {
            try {
                XWPFParagraph paragraph = (XWPFParagraph) wordDocument.getBodyElements().get(i);
                bodyElements.add(new ParagraphWrapper(paragraph));
            }
            catch (ClassCastException e) {
                XWPFTable table = (XWPFTable) wordDocument.getBodyElements().get(i);
                XWPFParagraph tableTextAsParagraph = new XWPFDocument().createParagraph();
                tableTextAsParagraph.createRun();
                XWPFRun createdRun = tableTextAsParagraph.getRuns().get(0);
                createdRun.setText(table.getText());
                createdRun.setStyle("BodyText");
                bodyElements.add(new ParagraphWrapper(tableTextAsParagraph));
            }
        }
        return bodyElements;
    }

    private boolean isWordDocumentEmpty() {
        return wordDocument == null;
    }

    private static boolean paragraphHasContent(ParagraphWrapper paragraphWrapper) {
        String paragraphText = paragraphWrapper.getParagraph().getText().trim();
        return !(paragraphText.isEmpty());
    }

    public Map<Integer, Integer> getPostParagraphBlanks() {
        Map<Integer, Integer> postParagraphBlanks = new HashMap<>();
        int actualParagraph = 0;
        List<ParagraphWrapper> paragraphWrappers = getRawParagraphs(wordDocument);
        for (ParagraphWrapper paragraph : paragraphWrappers) {
            boolean hasContent = paragraphHasContent(paragraph);
            if (hasContent) {
                postParagraphBlanks.put(actualParagraph, 1);
                actualParagraph++;
            }
            if (!hasContent && followsActualParagraph(postParagraphBlanks))
                increaseParagraphBlanksCount(postParagraphBlanks);
        }
        return postParagraphBlanks;
    }

    private static boolean followsActualParagraph(Map<Integer, Integer> blanksAfterParagraph) {
        return blanksAfterParagraph.size() != 0;
    }

    private static void increaseParagraphBlanksCount(Map<Integer, Integer> blanksAfterParagraph) {
        int lastParagraphIndex = blanksAfterParagraph.size() - 1;
        int currentBlanks = blanksAfterParagraph.get(lastParagraphIndex);
        blanksAfterParagraph.put(lastParagraphIndex, currentBlanks + 1);
    }

    public static Map<Integer, Boolean> getNumberedParagraphs(List<ParagraphWrapper> paragraphWrappers) {
        Map<Integer, Boolean> numberedParagraphs = new HashMap<>();
        for (int index = 0; index < paragraphWrappers.size(); index++) {
            XWPFParagraph paragraph = paragraphWrappers.get(index).getParagraph();
            String paragraphStyle = paragraph.getStyle();
            boolean hasNumbering = NumberingParser.paragraphHasNumbering(paragraph, paragraphStyle);
            numberedParagraphs.put(index, hasNumbering);
        }
        return numberedParagraphs;
    }

    public Map<Integer, Boolean> getNumberedParagraphs() {
        return isWordDocumentEmpty() ? new HashMap<>() : numberedParagraphs;
    }

    public static Map<Integer, UnitNumbering> getUnitNumberings(XWPFDocument wordDocument,
                                                                List<ParagraphWrapper> paragraphWrappers) {
        if (wordDocument == null) return new HashMap<>();

        NumberingParser numberingParser = new NumberingParser(wordDocument.getNumbering());
        List<XWPFParagraph> paragraphs = paragraphWrappers.stream()
                                            .map(ParagraphWrapper::getParagraph)
                                            .collect(Collectors.toList());
        return numberingParser.getParagraphsNumbering(paragraphs);
    }

    public Map<Integer, UnitNumbering> getUnitNumberings() {
        return isWordDocumentEmpty() ? new HashMap<>() : unitNumberings;
    }

    public List<String> getParagraphTexts() {

        if (isWordDocumentEmpty()) return new ArrayList<>();

        List<String> textWithoutNumbering = getParagraphTextsWithoutNumbering();
        List<String> withNumbering = new ArrayList<>();

        for (int i = 0; i < textWithoutNumbering.size(); i++) {
            String text = textWithoutNumbering.get(i);

            if (numberedParagraphs.get(i))
                text = unitNumberings.get(i).current + text;

            withNumbering.add(text.trim());
        }
        return withNumbering;
    }

    public List<String> getParagraphTextsWithoutNumbering() {
        if (isWordDocumentEmpty()) return new ArrayList<>();

        return getNonEmptyParagraphs().stream()
                .map(p -> p.getParagraph().getText().trim())
                .collect(Collectors.toList());
    }
}
