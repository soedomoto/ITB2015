package com.soedomoto.vrp.ws.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 07/01/17.
 */
@DatabaseTable
public class DistanceMatrix {
    @DatabaseField
    public long locationA;
    @DatabaseField
    public long locationB;
    @DatabaseField
    public double distance;
    @DatabaseField
    public double duration;
}
