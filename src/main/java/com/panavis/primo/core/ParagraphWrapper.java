package com.panavis.primo.core;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;
import java.util.stream.Collectors;

public class ParagraphWrapper {

    private XWPFParagraph paragraph;

    ParagraphWrapper(XWPFParagraph paragraph) { this.setParagraph(paragraph); }

    XWPFParagraph getParagraph() {
        return paragraph;
    }

    private void setParagraph(XWPFParagraph paragraph) {
        this.paragraph = paragraph;
    }

    public List<RunWrapper> getRuns() {
        List<XWPFRun> runs = getParagraph().getRuns();
        return runs.stream()
                    .map(RunWrapper::new)
                    .collect(Collectors.toList());
    }

    public int getIndentationLeft() {
        return paragraph.getIndentationLeft();
    }
}
