package com.soedomoto.vrp.ws.broker;

import java.sql.SQLException;

/**
 * Created by soedomoto on 08/01/17.
 */
public abstract interface BrokerListener {
    public void finish() throws SQLException;
}
