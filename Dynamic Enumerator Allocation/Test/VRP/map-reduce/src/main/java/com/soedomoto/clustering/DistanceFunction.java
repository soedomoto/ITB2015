package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Point;

/**
 * Created by soedomoto on 27/12/16.
 */
public interface DistanceFunction {
    public double distance(Point a, Point b);
}
