package com.soedomoto.tsp;

import com.soedomoto.clustering.model.Point;

import java.util.List;

/**
 * Created by soedomoto on 31/12/16.
 */
public interface TspListener {
    public void bestSolutionFound(List<Point> path, double score, long time);
    public void onStart(List<Point> initialPath, double initialScore, long startTime);
    public void onFinish(List<Point> path, double score, long time);
}
