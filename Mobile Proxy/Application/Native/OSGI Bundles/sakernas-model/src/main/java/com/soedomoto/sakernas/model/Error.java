package com.soedomoto.sakernas.model;

/**
 * Created by soedomoto on 08/11/16.
 */
public class Error {
    private String docId;
    private String field;
    private String error;
    private String threatment;

    public Error(String docId, String field) {
        this.docId = docId;
        this.field = field;
    }

    public String getDocId() {
        return docId;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }

    public Error setError(String error) {
        this.error = error;
        return this;
    }

    public String getThreatment() {
        return threatment;
    }

    public Error setThreatment(String threatment) {
        this.threatment = threatment;
        return this;
    }
}
