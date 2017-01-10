package com.soedomoto.vrp.ws;

import com.j256.ormlite.dao.Dao;
import com.soedomoto.vrp.ws.broker.AbstractBroker;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.Enumerator;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soedomoto on 07/01/17.
 */
@Path("/")
public class Api {
    @Context
    ServletContext context;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response map() throws SQLException {
        Map<String, Object> model = new HashMap();
        return Response.ok(new Viewable("/map.ftl", model)).build();
    }

    @GET
    @Path("/enumerators")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enumerators() throws SQLException {
        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        return Response.ok(enumeratorDao.queryForAll()).build();
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response locations() throws SQLException {
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        return Response.ok(censusBlockDao.queryForAll()).build();
    }

    @GET
    @Path("/subscribe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void subscribe(@PathParam("id") final String enumeratorId, @Suspended final AsyncResponse asyncResponse)
            throws SQLException {
        AbstractBroker broker = (AbstractBroker) context.getAttribute("broker");
        broker.subscribe(enumeratorId, asyncResponse);
    }

    @GET
    @Path("/visit/{customer}/by/{enumerator}")
    @Produces(MediaType.APPLICATION_JSON)
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
}
