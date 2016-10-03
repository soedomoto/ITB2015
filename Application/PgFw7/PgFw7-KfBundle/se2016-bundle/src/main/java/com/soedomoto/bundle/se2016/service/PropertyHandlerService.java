package com.soedomoto.bundle.se2016.service;

/**
 * Created by soedomoto on 02/09/16.
 */
public interface PropertyHandlerService {
    public String getJdbcUrl();
    public String getJdbcUsername();
    public String getJdbcPassword();
    public String getRealHost();
    public String getContextPath();
    public String getDataDirectory();
}
