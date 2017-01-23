package com.soedomoto.vrp.pubsub;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.*;

/**
 * Created by soedomoto on 19/01/17.
 */
public abstract class ChannelWatcher {
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    protected SortedSet<String> availableChannels = new TreeSet();
    private final Jedis jedis;

    public ChannelWatcher(Jedis jedis) {
        this.jedis = jedis;
    }

    public ChannelWatcher watch() {
        Executors.newCachedThreadPool().execute(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        ScheduledFuture<?> task = executor
                                .schedule(new WatchExecutor(), 1, TimeUnit.SECONDS);
                        task.get();
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    }
                }
            }
        });

        return this;
    }

    private class WatchExecutor implements Runnable {
        public void run() {
            List<String> channels = jedis.pubsubChannels("*");

            for(String channel : availableChannels) {
                if(! channels.contains(channel)) {
                    availableChannels.remove(channel);
                    onChannelRemoved(channel);
                }
            }

            for(String channel : channels) {
                if(availableChannels.add(channel)) {
                    onChannelAdded(channel);
                }
            }
        }
    }

    public abstract void onChannelAdded(String channel);
    public abstract void onChannelRemoved(String channel);
}
