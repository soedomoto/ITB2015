package com.soedomoto.vrp.ws;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by soedomoto on 07/01/17.
 */
@Path("/")
public class App {
    @Context
    ServletContext context;

    @GET
    @Produces("text/html")
    public Response map() throws SQLException {
        Map<String, Object> model = new HashMap();
        return Response.ok(new Viewable("/map.ftl", model)).build();
    }

    @GET
    @Path("/enumerators")
    @Produces("application/json")
    public Response enumerators() throws SQLException {
        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        return Response.ok(enumeratorDao.queryForAll()).build();
    }

    @GET
    @Path("/locations")
    @Produces("application/json")
    public Response locations() throws SQLException {
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        return Response.ok(censusBlockDao.queryForAll()).build();
    }

    @GET
    @Path("/subscribe/{id}")
    @Produces("application/json")
    @ManagedAsync
    public void subscribe(@PathParam("id") final String enumeratorId, @Suspended final AsyncResponse asyncResponse)
            throws SQLException {

        Broker broker = (Broker) context.getAttribute("broker");
        broker.subscribe(enumeratorId, asyncResponse);
    }

    @GET
    @Path("/visit/{customer}/by/{enumerator}")
    @Produces("application/json")
    @ManagedAsync
    public void visit(@PathParam("customer") final String customerId, @PathParam("enumerator") final String enumeratorId,
                      @Suspended final AsyncResponse asyncResponse) throws SQLException {

        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        CensusBlock bs = censusBlockDao.queryForId(Long.valueOf(customerId));
        bs.visitedBy = Long.valueOf(enumeratorId);
        int status = censusBlockDao.update(bs);

        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        Enumerator enumerator = enumeratorDao.queryForId(Long.valueOf(enumeratorId));
        enumerator.depot = Long.valueOf(customerId);
        enumeratorDao.update(enumerator);

        asyncResponse.resume(status);
    }

    public static void main(String[] args) throws URISyntaxException {
        // Static file Handler
        ServletHolder resourceServlet = new ServletHolder("default", DefaultServlet.class);
        resourceServlet.setInitParameter("resourceBase", new File(App.class.getResource("/assets").toURI()).getAbsolutePath());
        resourceServlet.setInitParameter("dirAllowed", "true");
        resourceServlet.setInitParameter("pathInfoOnly", "true");

        ServletContextHandler resourceContextHandler = new ServletContextHandler();
        resourceContextHandler.setContextPath("/assets");
        resourceContextHandler.addServlet(resourceServlet, "/*");

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
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

            public void contextInitialized(ServletContextEvent sce) {
                try {
                    // Add persistence storage to context
                    ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:h2:./vrp");

                    Dao<Enumerator, Long> enumeratorDao = DaoManager.createDao(connectionSource, Enumerator.class);
                    Dao<CensusBlock, Long> censusBlockDao = DaoManager.createDao(connectionSource, CensusBlock.class);
                    Dao<DistanceMatrix, Long> distanceMatrixDao = DaoManager.createDao(connectionSource, DistanceMatrix.class);

                    TableUtils.createTableIfNotExists(connectionSource, Enumerator.class);
                    TableUtils.createTableIfNotExists(connectionSource, CensusBlock.class);
                    TableUtils.createTableIfNotExists(connectionSource, DistanceMatrix.class);

                    sce.getServletContext().setAttribute("enumeratorDao", enumeratorDao);
                    sce.getServletContext().setAttribute("censusBlockDao", censusBlockDao);
                    sce.getServletContext().setAttribute("distanceMatrixDao", distanceMatrixDao);

                    SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource("solver-config.xml");
                    Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();
                    sce.getServletContext().setAttribute("solver", solver);

                    // Add executor to context
                    Broker broker = new Broker(executor, sce.getServletContext());
                    sce.getServletContext().setAttribute("broker", broker);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            public void contextDestroyed(ServletContextEvent sce) {
                executor.shutdown();
            }
        });


        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(jerseyContextHandler);
        handlers.addHandler(resourceContextHandler);

        QueuedThreadPool threadPool = new QueuedThreadPool(100, 10);
        Server server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(2222);
        server.setConnectors(new Connector[]{connector});
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
