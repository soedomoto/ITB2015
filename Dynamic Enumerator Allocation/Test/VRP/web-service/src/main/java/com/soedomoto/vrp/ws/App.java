package com.soedomoto.vrp.ws;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.vrp.ws.broker.AbstractBroker;
import com.soedomoto.vrp.ws.broker.JSpritBroker;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import com.soedomoto.vrp.ws.model.Subscriber;
import com.soedomoto.vrp.ws.ws.EventServlet;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 10/01/17.
 */
public class App {
    public static void main(String[] args) throws URISyntaxException, IOException {
        final String BASE_DIR = new File(".." + File.separator + "Output" + File.separator +
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date())).getAbsolutePath();

        // Copy database file
        final String DB_NAME = BASE_DIR + File.separator + "vrp";
        FileUtils.copyFile(new File(Api.class.getResource("/vrp.mv.db").toURI()), new File(DB_NAME + ".mv.db"));

        // Static file Handler
        ServletHolder resourceServlet = new ServletHolder("default", DefaultServlet.class);
        resourceServlet.setInitParameter("resourceBase", new File(Api.class.getResource("/assets").toURI()).getAbsolutePath());
        resourceServlet.setInitParameter("dirAllowed", "true");
        resourceServlet.setInitParameter("pathInfoOnly", "true");

        ServletContextHandler resourceContextHandler = new ServletContextHandler();
        resourceContextHandler.setContextPath("/assets");
        resourceContextHandler.addServlet(resourceServlet, "/*");

        // Websocket
        ServletHolder wsEventServlet = new ServletHolder("ws-events", EventServlet.class);

        ServletContextHandler wsContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        wsContextHandler.setContextPath("/ws");
        wsContextHandler.addServlet(wsEventServlet, "/*");

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
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            public void contextInitialized(ServletContextEvent sce) {
                try {
                    // Add persistence storage to context
                    ConnectionSource connectionSource = new JdbcConnectionSource(String.format("jdbc:h2:%s;DB_CLOSE_ON_EXIT=FALSE", DB_NAME));

                    Dao<Enumerator, Long> enumeratorDao = DaoManager.createDao(connectionSource, Enumerator.class);
                    Dao<CensusBlock, Long> censusBlockDao = DaoManager.createDao(connectionSource, CensusBlock.class);
                    Dao<DistanceMatrix, Long> distanceMatrixDao = DaoManager.createDao(connectionSource, DistanceMatrix.class);
                    Dao<Subscriber, Long> subscriberDao = DaoManager.createDao(connectionSource, Subscriber.class);

                    TableUtils.createTableIfNotExists(connectionSource, Enumerator.class);
                    TableUtils.createTableIfNotExists(connectionSource, CensusBlock.class);
                    TableUtils.createTableIfNotExists(connectionSource, DistanceMatrix.class);
                    TableUtils.createTableIfNotExists(connectionSource, Subscriber.class);

                    sce.getServletContext().setAttribute("enumeratorDao", enumeratorDao);
                    sce.getServletContext().setAttribute("censusBlockDao", censusBlockDao);
                    sce.getServletContext().setAttribute("distanceMatrixDao", distanceMatrixDao);
                    sce.getServletContext().setAttribute("subscriberDao", subscriberDao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Add executor to context
                AbstractBroker broker = new JSpritBroker(executor, sce.getServletContext());
                sce.getServletContext().setAttribute("broker", broker);

                // Set logger location
                sce.getServletContext().setAttribute("clientLogDir", BASE_DIR + File.separator + "client_log");
                sce.getServletContext().setAttribute("serverLogDir", BASE_DIR + File.separator + "server_log");
            }

            public void contextDestroyed(ServletContextEvent sce) {
                executor.shutdown();
            }
        });


        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(jerseyContextHandler);
        handlers.addHandler(resourceContextHandler);
        handlers.addHandler(wsContextHandler);

        Server server = new Server(2222);
        server.setHandler(handlers);

        try {
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
}
