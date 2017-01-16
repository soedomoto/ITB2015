package com.soedomoto.vrp.ws.api;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.soedomoto.vrp.ws.broker.HttpAbstractBroker;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.Enumerator;
import org.glassfish.jersey.server.ManagedAsync;

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
import java.util.*;

/**
 * Created by soedomoto on 07/01/17.
 */
@Path("/")
public class HttpPubSub {
    @Context
    ServletContext context;

    @GET
    @Path("/enumerators")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enumerators() throws SQLException {
        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        List<Enumerator> enumerators = enumeratorDao.queryForAll();

        for(Enumerator e: enumerators) {
            CensusBlock currDepot = censusBlockDao.queryForId(e.getDepot());
            e.setLat(currDepot.getLat());
            e.setLon(currDepot.getLon());
        }

        return Response.ok(enumerators).build();
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response locations() throws SQLException {
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        return Response.ok(censusBlockDao.queryForAll()).build();
    }

    @GET
    @Path("/visits/{enumerator}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response visits(@PathParam("enumerator") String enumeratorId) throws SQLException {
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");

        List<CensusBlock> visitedLocations = censusBlockDao.queryBuilder().where()
                .eq("visited_by", Long.valueOf(enumeratorId))
                .query();
        return Response.ok(visitedLocations).build();
    }

    @GET
    @Path("/routes/{enumerator}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response routes(@PathParam("enumerator") String enumeratorId) throws SQLException {
        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");

        List<Map<String, Object>> allServices = new ArrayList();

        Enumerator e = enumeratorDao.queryForId(Long.valueOf(enumeratorId));
        Map<String, Object> s = new HashMap();
        s.put("id", e.getId());
        s.put("lat", e.getLat());
        s.put("lon", e.getLon());
        allServices.add(s);

        QueryBuilder<CensusBlock, Long> qb = censusBlockDao.queryBuilder();
        qb.where().eq("visited_by", Long.valueOf(enumeratorId));
        qb.orderBy("visit_date", true);
        List<CensusBlock> visitedLocations = qb.query();

        for(CensusBlock bs : visitedLocations) {
            s = new HashMap();
            s.put("id", bs.getId());
            s.put("lat", bs.getLat());
            s.put("lon", bs.getLon());
            allServices.add(s);
        }

        return Response.ok(allServices).build();
    }

    @GET
    @Path("/subscribe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void subscribe(@PathParam("id") final String enumeratorId, @Suspended final AsyncResponse asyncResponse)
            throws SQLException {
        HttpAbstractBroker broker = (HttpAbstractBroker) context.getAttribute("broker");
        broker.subscribe(enumeratorId, asyncResponse);
    }

    @GET
    @Path("/visit/{customer}/by/{enumerator}/on/{timestamp}")
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void visit(@PathParam("customer") final String customerId, @PathParam("enumerator") final String enumeratorId,
                      @PathParam("timestamp") final String timestamp, @Suspended final AsyncResponse asyncResponse)
            throws SQLException {

        Date date = new Date(Long.valueOf(timestamp));

        Dao<CensusBlock, Long> censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        CensusBlock bs = censusBlockDao.queryForId(Long.valueOf(customerId));
        bs.setVisitedBy(Long.valueOf(enumeratorId));
        bs.setVisitDate(date);
        int status = censusBlockDao.update(bs);

        Dao<Enumerator, Long> enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        Enumerator enumerator = enumeratorDao.queryForId(Long.valueOf(enumeratorId));
        enumerator.setDepot(Long.valueOf(customerId));
        enumeratorDao.update(enumerator);

        asyncResponse.resume(bs);
    }
}
