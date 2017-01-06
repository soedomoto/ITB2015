package com.soedomoto.vrp;

/**
 * Created by soedomoto on 28/12/16.
 */
public class TerminationCriteria {
    private Integer vrpIterations;
    private Integer tspIterations;

    private Integer vrpTimeLimit;
    private Integer tspTimeLimit;

    public Integer getVrpIterations() {
        return vrpIterations;
    }

    public void setVrpIterations(Integer vrpIterations) {
        this.vrpIterations = vrpIterations;
    }

    public Integer getTspIterations() {
        return tspIterations;
    }

    public void setTspIterations(Integer tspIterations) {
        this.tspIterations = tspIterations;
    }

    public Integer getVrpTimeLimit() {
        return vrpTimeLimit;
    }

    public void setVrpTimeLimit(Integer vrpTimeLimit) {
        this.vrpTimeLimit = vrpTimeLimit;
    }

    public Integer getTspTimeLimit() {
        return tspTimeLimit;
    }

    public void setTspTimeLimit(Integer tspTimeLimit) {
        this.tspTimeLimit = tspTimeLimit;
    }
}
