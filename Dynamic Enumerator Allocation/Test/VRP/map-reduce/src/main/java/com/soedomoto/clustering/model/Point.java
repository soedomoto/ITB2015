package com.soedomoto.clustering.model;

public class Point {

    private String id;
    private double x = 0;
    private double y = 0;
    private int serviceTime = 0;
    private boolean visited = false;
    private Cluster cluster;

    public Point(String id, double x, double y) {
        this.setId(id);
        this.setX(x);
        this.setY(y);
    }

    public Point(double x, double y) {
        this.setId(String.format("%s-%s", x, y));
        this.setX(x);
        this.setY(y);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX()  {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setCluster(Cluster n) {
        this.cluster = n;
        n.addPoint(this);
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    public String toString() {
        return String.format("[%s, %s]", x, y);
    }
}