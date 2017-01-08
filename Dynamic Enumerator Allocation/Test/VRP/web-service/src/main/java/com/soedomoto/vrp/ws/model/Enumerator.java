package com.soedomoto.vrp.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 07/01/17.
 */
@DatabaseTable
public class Enumerator {
    @DatabaseField(id = true)
    public long id;
    @DatabaseField
    public double lat;
    @DatabaseField
    public double lon;
    @DatabaseField
    public long depot;
}
