package com.panavis.primo.core;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class RunWrapper {

    private XWPFRun run;

    RunWrapper(XWPFRun run) { this.run = run; }

    XWPFRun getRun() {
        return run;
    }

    public String getText() { return run.text(); }
}
