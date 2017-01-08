package com.soedomoto.vrp.ws.broker;

import com.j256.ormlite.dao.Dao;
import com.soedomoto.vrp.ws.model.CensusBlock;
import com.soedomoto.vrp.ws.model.DistanceMatrix;
import com.soedomoto.vrp.ws.model.Enumerator;

import javax.servlet.ServletContext;
import javax.ws.rs.container.AsyncResponse;
import java.util.LinkedList;
import java.util.List;
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

    protected List<Long> subscribers = new LinkedList();
    protected List<AsyncResponse> asyncResponses = new LinkedList();
    protected ScheduledFuture<?> currTask;
    protected BrokerListener listener;

    public AbstractBroker(ScheduledExecutorService executor, ServletContext context) {
        this.executor = executor;
        this.context = context;

        enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        matrixDao = (Dao<DistanceMatrix, Long>) context.getAttribute("distanceMatrixDao");
    }

    public void run() {}

    public void subscribe(String enumeratorId, AsyncResponse asyncResponse) {
        if(! subscribers.contains(Long.valueOf(enumeratorId))) {
            subscribers.add(Long.valueOf(enumeratorId));
            asyncResponses.add(asyncResponse);
        }

        if(subscribers.size() > 0 && (currTask == null || (currTask != null && currTask.isDone()))) {
            listener = new BrokerListener() {
                public void finish() {
                    if(subscribers.size() > 0) currTask = executor.schedule(AbstractBroker.this, 15, TimeUnit.SECONDS);
                }
            };

            currTask = executor.schedule(this, 15, TimeUnit.SECONDS);
        }
    }
}
