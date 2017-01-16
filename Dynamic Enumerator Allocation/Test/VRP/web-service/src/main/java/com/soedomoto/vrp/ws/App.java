package com.soedomoto.vrp.ws;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.vrp.ws.api.SsePubSub;
import com.soedomoto.vrp.ws.broker.HttpAbstractBroker;
import com.soedomoto.vrp.ws.broker.HttpJSpritBroker;
import com.soedomoto.vrp.ws.broker.SseAbstractBroker;
import com.soedomoto.vrp.ws.broker.SseJSpritBroker;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import com.soedomoto.vrp.ws.model.Subscriber;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 10/01/17.
 */
public class App {
    private final static Logger LOG = Logger.getLogger(App.class.getName());

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Dao<Enumerator, Long> enumeratorDao;
    private Dao<CensusBlock, Long> censusBlockDao;
    private Dao<DistanceMatrix, Long> distanceMatrixDao;
    private Dao<Subscriber, Long> subscriberDao;

    public App(CommandLine cmd) {
        cmd.getOptionValue("t");
        File outDir = new File(System.getProperty("user.dir"), cmd.getOptionValue("O"));
        String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());

        if(cmd.hasOption("t")) {
            outDir = new File(outDir, now);
        }

        final String BASE_DIR = outDir.getAbsolutePath();
        final String JDBC_URL = cmd.getOptionValue("db");

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(JDBC_URL);

            enumeratorDao = DaoManager.createDao(connectionSource, Enumerator.class);
            censusBlockDao = DaoManager.createDao(connectionSource, CensusBlock.class);
            distanceMatrixDao = DaoManager.createDao(connectionSource, DistanceMatrix.class);
            subscriberDao = DaoManager.createDao(connectionSource, Subscriber.class);

            TableUtils.createTableIfNotExists(connectionSource, Enumerator.class);
            TableUtils.createTableIfNotExists(connectionSource, CensusBlock.class);
            TableUtils.createTableIfNotExists(connectionSource, DistanceMatrix.class);
            TableUtils.createTableIfNotExists(connectionSource, Subscriber.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ServletContextHandler sseContextHandler = new ServletContextHandler();
        sseContextHandler.setContextPath("/sse");
        ServletHolder holder = new ServletHolder(new SsePubSub());
        holder.setAsyncSupported(true);
        sseContextHandler.addServlet(holder, "/subscribe");
        sseContextHandler.addEventListener(new ServletContextListener() {
            public void contextInitialized(ServletContextEvent sce) {
                // Add persistence storage to context
                sce.getServletContext().setAttribute("enumeratorDao", enumeratorDao);
                sce.getServletContext().setAttribute("censusBlockDao", censusBlockDao);
                sce.getServletContext().setAttribute("distanceMatrixDao", distanceMatrixDao);
                sce.getServletContext().setAttribute("subscriberDao", subscriberDao);

                // Add executor to context
                SseAbstractBroker broker = new SseJSpritBroker(executor, sce.getServletContext());
                sce.getServletContext().setAttribute("broker", broker);

                // Set logger location
                sce.getServletContext().setAttribute("clientLogDir", BASE_DIR + File.separator + "client_log");
                sce.getServletContext().setAttribute("serverLogDir", BASE_DIR + File.separator + "server_log");
            }

            public void contextDestroyed(ServletContextEvent sce) {}
        });


        // Jersey Servlet Handler
        ResourceConfig config = new ResourceConfig();
        config.register(MultiPartFeature.class);
        config.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "html");
        config.property(ServletProperties.FILTER_FORWARD_ON_404, true);
        config.register(FreemarkerMvcFeature.class);
        config.packages("com.soedomoto.vrp.ws");

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

                // Add executor to context
                HttpAbstractBroker broker = new HttpJSpritBroker(executor, sce.getServletContext());
                sce.getServletContext().setAttribute("broker", broker);

                // Set logger location
                sce.getServletContext().setAttribute("clientLogDir", BASE_DIR + File.separator + "client_log");
                sce.getServletContext().setAttribute("serverLogDir", BASE_DIR + File.separator + "server_log");
            }

            public void contextDestroyed(ServletContextEvent sce) {}
        });


        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(jerseyContextHandler);
//        handlers.addHandler(resourceContextHandler);
//        handlers.addHandler(wsContextHandler);
        handlers.addHandler(sseContextHandler);

        // Initialize server
        int port = 8080;
        if(cmd.hasOption("P")) {
            port = Integer.parseInt(cmd.getOptionValue("P"));
        }

        Server server = new Server(port);
        server.setHandler(handlers);

        try {
            LOG.info(String.format("Server started at *:%s", port));
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option dbOpt = new Option("db", "jdbc-url", true, "JDBC URL of database");
        dbOpt.setRequired(true);
        options.addOption(dbOpt);

        Option outOpt = new Option("O", "output-dir", true, "Output directory");
        outOpt.setRequired(true);
        options.addOption(outOpt);

        Option portOpt = new Option("P", "port", true, "Port of server");
        portOpt.setRequired(false);
        options.addOption(portOpt);

        Option tsOpt = new Option("t", "use-timestamp", false, "Append timestamp to output directory");
        tsOpt.setRequired(false);
        options.addOption(tsOpt);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse( options, args);
            new App(cmd);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
        }
    }
}
