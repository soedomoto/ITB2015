package com.soedomoto.optaplanner;

import org.optaplanner.examples.vehiclerouting.domain.Customer;

/**
 * Created by soedomoto on 03/01/17.
 */
public class TCustomer extends Customer {
    boolean isVisited = false;

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}
