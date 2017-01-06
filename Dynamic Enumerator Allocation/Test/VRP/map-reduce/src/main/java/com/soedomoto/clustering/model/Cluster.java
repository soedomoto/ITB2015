package com.soedomoto.clustering.model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    public List<Point> points;
    public Point centroid;
    public String id;

    //Creates a new Cluster
    public Cluster(String id) {
        this.id = id;
        this.points = new ArrayList();
        this.centroid = null;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
        if(point.getCluster() == null) point.setCluster(this);
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public String getId() {
        return id;
    }

    public void clear() {
        points.clear();
    }

    @Override
    public String toString() {
        return String.format("{id: %s, centroid: %s, point_count: %s}", id, centroid, points.size());
    }
}
