package com.soedomoto.vrp.pubsub;

import com.google.gson.Gson;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.vrp.pubsub.model.CensusBlock;
import com.soedomoto.vrp.pubsub.model.DistanceMatrix;
import com.soedomoto.vrp.pubsub.model.Enumerator;
import com.soedomoto.vrp.pubsub.model.Subscriber;
import com.soedomoto.vrp.pubsub.solver.JSpritVRPSolver;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 19/01/17.
 */
public class App {
    private final static Logger LOG = Logger.getLogger(App.class.getName());

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(15);
    private SortedSet<String> beingProcessedChannels = new TreeSet();
    private Map<String, Map<String, Object>> cachedChannelResult = new HashMap();

    private Jedis jPub;

    private Dao<Enumerator, Long> enumeratorDao;
    private Dao<CensusBlock, Long> censusBlockDao;
    private Dao<DistanceMatrix, Long> distanceMatrixDao;
    private Dao<Subscriber, Long> subscriberDao;

    private int maxIteration = 500;

    public App(CommandLine cmd) {
        File outDir = new File(System.getProperty("user.dir"), cmd.getOptionValue("O"));
        String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());
        if(cmd.hasOption("t")) outDir = new File(outDir, now);

        if(cmd.hasOption("I")) maxIteration = Integer.parseInt(cmd.getOptionValue("I"));

        final String BASE_DIR = outDir.getAbsolutePath();
        final String JDBC_URL = cmd.getOptionValue("db");
        final String BROKER_URL = cmd.getOptionValue("B");

        try {
            App.this.setupDAO(JDBC_URL);

            // Pubsub App
            jPub = new Jedis(new URI(BROKER_URL));
            new ChannelWatcher(jPub) {
                public void onChannelAdded(String channel) {
                    try {
                        App.this.onChannelAdded(channel);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                public void onChannelRemoved(String channel) {
                    try {
                        App.this.onChannelRemoved(channel);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.watch();


            // WS App
            ResourceConfig config = new ResourceConfig();
            config.property(ServletProperties.FILTER_FORWARD_ON_404, true);
            config.packages("com.soedomoto");

            ServletContextHandler jerseyContextHandler = new ServletContextHandler();
            jerseyContextHandler.setContextPath("/vrp");
            jerseyContextHandler.addServlet(new ServletHolder(new ServletContainer(config)), "/*");
            jerseyContextHandler.addEventListener(new ServletContextListener() {
                public void contextInitialized(ServletContextEvent sce) {
                    // Add persistence storage to context
                    sce.getServletContext().setAttribute("enumeratorDao", enumeratorDao);
                    sce.getServletContext().setAttribute("censusBlockDao", censusBlockDao);
                    sce.getServletContext().setAttribute("distanceMatrixDao", distanceMatrixDao);
                    sce.getServletContext().setAttribute("subscriberDao", subscriberDao);
                }

                public void contextDestroyed(ServletContextEvent sce) {}
            });

            HandlerCollection handlers = new HandlerCollection();
            handlers.addHandler(jerseyContextHandler);

            Server server = new Server(8080);
            server.setHandler(handlers);

            try {
                LOG.info(String.format("Server started at *:%s", 8080));
                server.start();
                server.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                server.destroy();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupDAO(String JDBC_URL) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource(JDBC_URL);
        enumeratorDao = DaoManager.createDao(connectionSource, Enumerator.class);
        censusBlockDao = DaoManager.createDao(connectionSource, CensusBlock.class);
        distanceMatrixDao = DaoManager.createDao(connectionSource, DistanceMatrix.class);
        subscriberDao = DaoManager.createDao(connectionSource, Subscriber.class);

        TableUtils.createTableIfNotExists(connectionSource, Enumerator.class);
        TableUtils.createTableIfNotExists(connectionSource, CensusBlock.class);
        TableUtils.createTableIfNotExists(connectionSource, DistanceMatrix.class);
        TableUtils.createTableIfNotExists(connectionSource, Subscriber.class);
    }

    private void onChannelAdded(String channel) throws SQLException {
        LOG.debug(String.format("%s channel added", channel));

        if(cachedChannelResult.keySet().contains(channel)) {
            Map<String, Object> result = cachedChannelResult.get(channel);
            Long receivers = publish(channel, result);
            LOG.debug(String.format("Cached result is published to channel %s. Number receivers = %s", channel, receivers));

            return;
        }

        if(! beingProcessedChannels.contains(channel)) {
            executor.submit(new JSpritVRPSolver(App.this, channel) {
                public void onStarted(String channel) {
                    LOG.debug(String.format("Start solving channel %s", channel));
                    beingProcessedChannels.add(channel);
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

                public void onFinished(String channel) {
                    LOG.debug(String.format("Finish solving channel %s", channel));
                    beingProcessedChannels.remove(channel);
                }
            });
        }
    }

    private Long publish(String channel, Map<String, Object> solutionMap) {
        String result = new Gson().toJson(solutionMap);
        Long receivers = jPub.publish(channel, result);

        if(receivers > 0) {
            try {
                CensusBlock bs = censusBlockDao.queryForId(Long.valueOf(String.valueOf(solutionMap.get("location"))));
                bs.setAssignedTo((long) 1000);
                bs.setAssignDate(new Date());
                censusBlockDao.update(bs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return receivers;
    }

    private void onChannelRemoved(String channel) throws SQLException {
        LOG.debug(String.format("%s channel removed", channel));
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option dbOpt = new Option("db", "jdbc-url", true, "JDBC URL of database");
        dbOpt.setRequired(true);
        options.addOption(dbOpt);

        Option outOpt = new Option("O", "output-dir", true, "Output directory");
        outOpt.setRequired(true);
        options.addOption(outOpt);

        Option portOpt = new Option("B", "broker-url", true, "Redis broker URL");
        portOpt.setRequired(false);
        options.addOption(portOpt);

        Option itOpt = new Option("I", "iteration", true, "Number of maxIteration in searching solution");
        itOpt.setRequired(false);
        options.addOption(itOpt);

        Option tsOpt = new Option("t", "use-timestamp", false, "Append timestamp to output directory");
        tsOpt.setRequired(false);
        options.addOption(tsOpt);

        Option helpOpt = new Option("h", "help", false, "Print help");
        helpOpt.setRequired(false);
        options.addOption(helpOpt);

        String header = "Run location recommendation server\n\n";
        String footer = "\nPlease report issues at soedomoto@gmail.com";

        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse( options, args);

            if(cmd.hasOption("h")) {
                formatter.printHelp("vrp-publisher", header, options, footer, true);
                return;
            }

            new App(cmd);
        } catch (ParseException e) {
            LOG.error(e.getMessage());

            formatter.printHelp("vrp-publisher", header, options, footer, true);
        }
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public Dao<Enumerator, Long> getEnumeratorDao() {
        return enumeratorDao;
    }

    public void setEnumeratorDao(Dao<Enumerator, Long> enumeratorDao) {
        this.enumeratorDao = enumeratorDao;
    }

    public Dao<CensusBlock, Long> getCensusBlockDao() {
        return censusBlockDao;
    }

    public void setCensusBlockDao(Dao<CensusBlock, Long> censusBlockDao) {
        this.censusBlockDao = censusBlockDao;
    }

    public Dao<DistanceMatrix, Long> getDistanceMatrixDao() {
        return distanceMatrixDao;
    }

    public void setDistanceMatrixDao(Dao<DistanceMatrix, Long> distanceMatrixDao) {
        this.distanceMatrixDao = distanceMatrixDao;
    }

    public Dao<Subscriber, Long> getSubscriberDao() {
        return subscriberDao;
    }

    public void setSubscriberDao(Dao<Subscriber, Long> subscriberDao) {
        this.subscriberDao = subscriberDao;
    }
}
