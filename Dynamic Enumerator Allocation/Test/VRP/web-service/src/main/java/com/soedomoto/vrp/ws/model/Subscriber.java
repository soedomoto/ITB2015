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
    public Long id;
    @DatabaseField(columnName = "date_added")
    public Date dateAdded;
    @DatabaseField
    public Long subscriber;
    @DatabaseField(columnName = "is_processed", defaultValue = "false")
    public Boolean isProcessed;
}
