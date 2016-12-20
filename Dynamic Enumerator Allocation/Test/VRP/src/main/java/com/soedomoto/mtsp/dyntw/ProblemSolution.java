package com.soedomoto.mtsp.dyntw;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

/**
 * Created by soedomoto on 13/12/16.
 */
public class ProblemSolution {
    VehicleRoutingProblem problem;
    VehicleRoutingProblemSolution solution;

    public ProblemSolution(VehicleRoutingProblem problem, VehicleRoutingProblemSolution solution) {
        this.problem = problem;
        this.solution = solution;
    }

    public VehicleRoutingProblem getProblem() {
        return problem;
    }

    public VehicleRoutingProblemSolution getSolution() {
        return solution;
    }
}
