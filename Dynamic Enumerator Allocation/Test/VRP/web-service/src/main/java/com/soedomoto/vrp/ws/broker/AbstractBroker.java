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
import java.util.concurrent.*;

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

    protected long subscriberIndex = -1;
    protected List<Long> subscribers = new LinkedList();
    protected List<AsyncResponse> asyncResponses = new LinkedList();
    protected Map<Long, AsyncResponse> asyncResponseMap = new HashMap();
    protected Future<?> currTask;
    protected BrokerListener listener;

    public AbstractBroker(ScheduledExecutorService executor, ServletContext context) {
        this.executor = executor;
        this.context = context;

        enumeratorDao = (Dao<Enumerator, Long>) context.getAttribute("enumeratorDao");
        censusBlockDao = (Dao<CensusBlock, Long>) context.getAttribute("censusBlockDao");
        matrixDao = (Dao<DistanceMatrix, Long>) context.getAttribute("distanceMatrixDao");
        subscriberDao = (Dao<Subscriber, Long>) context.getAttribute("subscriberDao");

        String[] maxSubIds = new String[0];
        try {
            maxSubIds = subscriberDao.queryBuilder().selectRaw("MAX(id)").queryRawFirst();
            if(maxSubIds != null && maxSubIds[0] != null) subscriberIndex = Long.parseLong(maxSubIds[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() {}

    public void subscribe(String enumeratorId, AsyncResponse asyncResponse) throws SQLException {
        QueryBuilder<Subscriber, Long> qb = subscriberDao.queryBuilder();
        Subscriber s = qb.where()
                .eq("subscriber", enumeratorId).and()
                .eq("is_processed", false).queryForFirst();

        if(s == null) {
            s = new Subscriber();
            s.setSubscriber(Long.valueOf(enumeratorId));
            s.setDateAdded(new Date());
            int it = subscriberDao.create(s);
            if(it >= 1) {
                asyncResponseMap.put(s.getId(), asyncResponse);
            }
        } else {
            asyncResponseMap.put(s.getId(), asyncResponse);
        }

        if(asyncResponseMap.size() > 0 && (currTask == null || (currTask != null && currTask.isDone()))) {
            listener = new BrokerListener() {
                public void finish() throws SQLException {
                    List<Subscriber> ss = subscriberDao.queryBuilder().where().eq("is_processed", false).query();

                    try {
                        currTask.get(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        currTask.cancel(true);
                    } catch (ExecutionException e) {
                        currTask.cancel(true);
                    } catch (TimeoutException e) {
                        currTask.cancel(true);
                    }

                    System.gc();

                    if(ss.size() > 0) currTask = executor.submit(AbstractBroker.this);
                }
            };

            currTask = executor.schedule(this, 15, TimeUnit.SECONDS);
        }
    }
}
