package com.fratics.precis.fis.app;

import com.fratics.precis.base.OutputObject;

public class PrecisOutputObject extends OutputObject {
    private static final long serialVersionUID = 8259727611192043540L;

    public String toString() {
	return "PrecisOutputObject = {}\n";
    }

    public void loadResult(Object o) throws Exception {
    }

    public void init(int noOfFields) {
    }

    protected boolean isInitialized() {
	return false;
    }

}
