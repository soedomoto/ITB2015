package com.soedomoto.vrp.ws.broker;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;
import com.soedomoto.vrp.ws.model.Subscriber;

import javax.servlet.ServletContext;
import javax.ws.rs.container.AsyncResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by soedomoto on 08/01/17.
 */
public abstract class AbstractBroker implements Runnable {
    protected final ScheduledExecutorService executor;
    protected final ServletContext context;

    protected final Dao<Enumerator, Long> enumeratorDao;
    protected final Dao<CensusBlock, Long> censusBlockDao;
    protected final Dao<DistanceMatrix, Long> matrixDao;
    protected final Dao<Subscriber, Long> subscriberDao;

    protected List<Long> subscribers = new LinkedList();
    protected List<AsyncResponse> asyncResponses = new LinkedList();
    protected Map<Long, AsyncResponse> asyncResponseMap = new HashMap();
    protected ScheduledFuture<?> currTask;
    protected BrokerListener listener;

    public AbstractBroker(ScheduledExecutorService executor, ServletContext context) {
        this.executor = executor;
        this.context = context;

        enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        matrixDao = (Dao<DistanceMatrix, Long>) context.getAttribute("distanceMatrixDao");
        subscriberDao = (Dao<Subscriber, Long>) context.getAttribute("subscriberDao");
    }

    public void run() {}

    public void subscribe(String enumeratorId, AsyncResponse asyncResponse) throws SQLException {
        QueryBuilder<Subscriber, Long> qb = subscriberDao.queryBuilder();
        Subscriber s = qb.where()
                .eq("subscriber", enumeratorId).and()
                .eq("is_processed", false).queryForFirst();

        if(s == null) {
            s = new Subscriber();
            s.subscriber = Long.valueOf(enumeratorId);
            s.dateAdded = new Date();
            subscriberDao.create(s);
        }

        subscriberDao.refresh(s);
        asyncResponseMap.put(s.id, asyncResponse);

        List<Subscriber> subscribers = subscriberDao.queryBuilder().where().eq("is_processed", false).query();
        if(subscribers.size() > 0 && (currTask == null || (currTask != null && currTask.isDone()))) {
            listener = new BrokerListener() {
                public void finish() throws SQLException {
                    List<Subscriber> ss = subscriberDao.queryBuilder().where().eq("is_processed", false).query();
                    if(ss.size() > 0) currTask = executor.schedule(AbstractBroker.this, 15, TimeUnit.SECONDS);
                }
            };

            currTask = executor.schedule(this, 15, TimeUnit.SECONDS);
        }

//        if(! subscribers.contains(Long.valueOf(enumeratorId))) {
//            subscribers.add(Long.valueOf(enumeratorId));
//            asyncResponses.add(asyncResponse);
//        }
//
//        if(subscribers.size() > 0 && (currTask == null || (currTask != null && currTask.isDone()))) {
//            listener = new BrokerListener() {
//                public void finish() {
//                    if(subscribers.size() > 0) currTask = executor.schedule(AbstractBroker.this, 15, TimeUnit.SECONDS);
//                }
//            };
//
//            currTask = executor.schedule(this, 15, TimeUnit.SECONDS);
//        }
    }
}
