package com.soedomoto.bundle.account.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by soedomoto on 10/1/16.
 */
@DatabaseTable(tableName = "akun")
public class MAccount {
    @DatabaseField(id = true, useGetSet = true)
    private String username;
    @DatabaseField(useGetSet = true)
    private String password;

    public MAccount() {
    }

    public MAccount(String username) {
        this.username = username;
    }

    public MAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
