package com.soedomoto.vrp.pubsub;

import com.google.gson.Gson;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.soedomoto.vrp.App;
import com.soedomoto.vrp.model.CensusBlock;
import com.soedomoto.vrp.solver.JSpritVRPSolver;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 27/01/17.
 */
public class DepotWatcher extends ChannelWatcher {
    private final static Logger LOG = Logger.getLogger(DepotWatcher.class.getName());

    private final App app;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(15);
    private final List<String> beingProcessedDepots = new ArrayList();
    private final Map<String, Map<String, Object>> cachedChannelResult = new HashMap();

    public DepotWatcher(App app, String brokerUrl) throws URISyntaxException {
        super(brokerUrl);
        this.app = app;
    }

    public void onChannelAdded(String channel) {
        channel = channel.replace("depot.", "");

        LOG.debug(String.format("%s channel added", channel));

        if(cachedChannelResult.keySet().contains(channel)) {
            Map<String, Object> result = cachedChannelResult.get(channel);
            Long receivers = publish(channel, result);
            LOG.debug(String.format("Cached result is published to channel %s. Number receivers = %s", channel, receivers));

            return;
        }

        if(! beingProcessedDepots.contains(channel)) {
            executor.submit(new JSpritVRPSolver(app, channel) {
                public void onStarted(String channel, List<CensusBlock> depots, Collection<CensusBlock> locations) {
                    LOG.debug(String.format("Start solving channel %s", channel));
                    for(CensusBlock d: depots)
                        beingProcessedDepots.add(String.valueOf(d.getId()));
                }

                public void onSolution(String channel, Vehicle routeVehicle, Service activity, double duration, double serviceTime) {
                    Map<String, Object> solutionMap = new HashMap();
                    solutionMap.put("depot", routeVehicle.getStartLocation().getId());
                    solutionMap.put("depot-coord", new Double[] {
                            routeVehicle.getStartLocation().getCoordinate().getY(),
                            routeVehicle.getStartLocation().getCoordinate().getX()});
                    solutionMap.put("location", activity.getLocation().getId());
                    solutionMap.put("location-coord", new Double[] {
                            activity.getLocation().getCoordinate().getY(),
                            activity.getLocation().getCoordinate().getX()});
                    solutionMap.put("duration", duration);
                    solutionMap.put("service-time", serviceTime);

                    Long receivers = publish(channel, solutionMap);
                    LOG.debug(String.format("Result is published to channel %s. Number receivers = %s", channel, receivers));

                    cachedChannelResult.put(channel, solutionMap);
                }

                public void onFinished(String channel, List<CensusBlock> depots, Collection<CensusBlock> locations) {
                    LOG.debug(String.format("Finish solving channel %s", channel));
                    for(CensusBlock d: depots)
                        beingProcessedDepots.remove(String.valueOf(d.getId()));
                }
            });
        }
    }

    public void onChannelRemoved(String channel) {
        LOG.debug(String.format("%s channel removed", channel));
    }

    private Long publish(String channel, Map<String, Object> solutionMap) {
        channel = String.format("depot.%s", channel);

        String result = new Gson().toJson(solutionMap);
        long receivers = jedis.publish(channel, result);

        if(receivers > 0) {
            try {
                CensusBlock bs = app.getCensusBlockDao().queryForId(Long.valueOf(String.valueOf(solutionMap.get("location"))));
                bs.setAssignedTo((long) 1000);
                bs.setAssignDate(new Date());
                app.getCensusBlockDao().update(bs);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            cachedChannelResult.remove(channel);
        }

        return receivers;
    }
}
