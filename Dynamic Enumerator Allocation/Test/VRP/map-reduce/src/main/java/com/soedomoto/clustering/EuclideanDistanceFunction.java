package com.soedomoto.clustering;

import com.soedomoto.clustering.model.Point;

/**
 * Created by soedomoto on 27/12/16.
 */
public class EuclideanDistanceFunction implements DistanceFunction {
    public double distance(Point a, Point b) {
        return Math.sqrt(Math.pow((b.getY() - a.getY()), 2) + Math.pow((b.getX() - a.getX()), 2));
    }
}
