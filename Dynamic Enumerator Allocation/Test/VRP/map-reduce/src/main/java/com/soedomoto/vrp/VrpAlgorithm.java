package com.soedomoto.vrp;

import com.soedomoto.clustering.ClusteringAlgorithm;
import com.soedomoto.clustering.model.Point;

import java.util.List;

/**
 * Created by soedomoto on 28/12/16.
 */
public interface VrpAlgorithm {
    public void search();
    public void setCustomers(List<Point> customers);
    public void addCustomer(Point customer);
    public void setVehicles(List<Point> vehicles);
    public void addVehicle(Point vehicle);
    public void setClusteringAlgorithm(ClusteringAlgorithm clusteringAlgorithm);
    public void setTerminationCriteria(TerminationCriteria terminationCriteria);
}
