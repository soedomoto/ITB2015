package com.soedomoto.vrp.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 09/01/17.
 */
@DatabaseTable
public class Subscriber {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private Long id;
    @DatabaseField(columnName = "date_added")
    private Date dateAdded;
    @DatabaseField
    private Long subscriber;
    @DatabaseField(columnName = "is_processed", defaultValue = "false")
    private Boolean isProcessed;
    @DatabaseField(columnName = "is_committed", defaultValue = "false")
    private Boolean isCommitted;
    @DatabaseField
    private String response;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Long getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Long subscriber) {
        this.subscriber = subscriber;
    }

    public Boolean getProcessed() {
        return isProcessed;
    }

    public void setProcessed(Boolean processed) {
        isProcessed = processed;
    }

    public Boolean getCommitted() {
        return isCommitted;
    }

    public void setCommitted(Boolean committed) {
        isCommitted = committed;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
