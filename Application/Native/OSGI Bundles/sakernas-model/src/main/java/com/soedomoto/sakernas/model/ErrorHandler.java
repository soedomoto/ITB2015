package com.soedomoto.sakernas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soedomoto on 08/11/16.
 */
public class ErrorHandler {
    private final Ruta ruta;
    private List<Error> errors = new ArrayList();

    public ErrorHandler(Ruta ruta) {
        this.ruta = ruta;
    }

    public void addError(String field, String errorMsg, String threatmentMsg) {
        Error error = new Error(ruta.getID(), field).setError(errorMsg).setThreatment(threatmentMsg);
        errors.add(error);
    }
}
