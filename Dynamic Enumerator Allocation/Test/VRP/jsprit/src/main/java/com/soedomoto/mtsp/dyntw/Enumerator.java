package com.soedomoto.mtsp.dyntw;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by soedomoto on 13/12/16.
 */
public class Enumerator implements Runnable {
    private Logger logger;

    private String id;
    private Double[] depot;
    private Broker broker;
    private ProblemSolution problemSolution;

    private volatile boolean subscribing = true;

    public Enumerator(String id) {
        this.id = id;

        this.logger = Logger.getLogger(this.id);
        try {
            FileHandler fh = new FileHandler(String.format("output/mtsp_dyn_time_windows_enumerator_%s.log", this.id));
            this.logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Location currLocation() {
        VehicleRoute currRoute = null;
        List<TourActivity> activities = new ArrayList();
        for (VehicleRoute route : problemSolution.getSolution().getRoutes()) {
            if (route.getVehicle().getId().equalsIgnoreCase(this.id)) {
                currRoute = route;
                activities = new ArrayList(route.getActivities());
            }
        }

        if (activities.size() > 0) {
            return activities.get(0).getLocation();
        }
        return null;
    }

    public void run() {
        while (broker.isRunning()) {
            try {
                Location location = currLocation();
                if(location != null) {
                    CensusBlock bs = broker.getCensusBlock(location);

                    if(bs != null && !bs.isVisited()) {
                        logger.info(String.format("%s Start collecting data at %s,%s", id, bs.getLat(), bs.getLon()));
                        broker.publishVisit(this, bs);

                        Thread.sleep(bs.getServiceTime().longValue());

                        logger.info(String.format("%s Finish collecting data at %s,%s for %s", id, bs.getLat(),
                                bs.getLon(), bs.getServiceTime()));
                        setDepot(bs.getLat(), bs.getLon());

                        subscribing = broker.subscribeSolution(this);
                    }
                } else {
                    if(!subscribing) {
                        subscribing = broker.subscribeSolution(this);
                    }

                    Thread.sleep((long) 10000);
                }
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public String getId() {
        return id;
    }

    public Double[] getDepot() {
        return depot;
    }

    public Enumerator setDepot(Double lat, Double lon) {
        this.depot = new Double[] {lat, lon};
        return this;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public ProblemSolution getProblemSolution() {
        return problemSolution;
    }

    public void publishSolution(ProblemSolution problemSolution) {
        this.problemSolution = problemSolution;
    }

    public static List<Enumerator> readCsvData(String csvPath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(csvPath));

        // Skip header
        reader.readNext();

        // Read row
        List<Enumerator> enumerators = new ArrayList<Enumerator>();
        String[] row;
        while ((row = reader.readNext()) != null) {
            Enumerator enumerator = new Enumerator(row[0])
                    .setDepot(Double.parseDouble(row[2]), Double.parseDouble(row[1]));
            enumerators.add(enumerator);
        }

        return enumerators;
    }
}
