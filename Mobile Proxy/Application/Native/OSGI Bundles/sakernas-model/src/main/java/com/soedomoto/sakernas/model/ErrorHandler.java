package com.soedomoto.sakernas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soedomoto on 08/11/16.
 */
public class ErrorHandler {
    private final String id;
    private List<Error> errors = new ArrayList();

    public ErrorHandler(String id) {
        this.id = id;
    }

    public void addError(String field, String errorMsg, String threatmentMsg) {
        Error error = new Error(id, field).setError(errorMsg).setThreatment(threatmentMsg);
        errors.add(error);
    }
}
