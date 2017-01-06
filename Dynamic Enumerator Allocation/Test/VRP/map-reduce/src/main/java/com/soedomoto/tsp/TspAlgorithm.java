package com.soedomoto.tsp;

import com.soedomoto.clustering.model.Point;
import com.soedomoto.vrp.TerminationCriteria;

import java.util.List;

/**
 * Created by soedomoto on 30/12/16.
 */
public interface TspAlgorithm {
    public void solve(Point depot, List<Point> path);
    public void setListener(TspListener listener);
    public void setTerminationCriteria(TerminationCriteria terminationCriteria);
}
