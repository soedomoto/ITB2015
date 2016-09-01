package com.soedomoto.bundle.se2016.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by soedomoto on 8/14/16.
 */
@DatabaseTable(tableName = "pencacah")
public class MPencacah {
    @DatabaseField(id = true, columnName = "v202", useGetSet = true)
    private String id;
    @DatabaseField(columnName = "v201", useGetSet = true)
    private String nama;
    @DatabaseField(columnName = "v203", useGetSet = true)
    private String handphone;
    @DatabaseField(columnName = "last_update", useGetSet = true)
    private Date lastUpdate;

    public MPencacah() {}

    public MPencacah(String id, String nama, String handphone, Date lastUpdate) {
        this.id = id;
        this.nama = nama;
        this.handphone = handphone;
        this.setLastUpdate(lastUpdate);
    }

    public MPencacah(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHandphone() {
        return handphone;
    }

    public void setHandphone(String handphone) {
        this.handphone = handphone;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
