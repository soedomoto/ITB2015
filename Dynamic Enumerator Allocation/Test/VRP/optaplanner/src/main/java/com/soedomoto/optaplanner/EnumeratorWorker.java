package com.soedomoto.optaplanner;

import org.optaplanner.examples.vehiclerouting.domain.Customer;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by soedomoto on 02/01/17.
 */
public class EnumeratorWorker implements Runnable {
    private Logger logger;
    private SubscribeListener listener;

    private final Long enumeratorId;
    private TCustomer currCustomer;
    private volatile boolean subscribing = true;

    public EnumeratorWorker(Long enumeratorId, SubscribeListener listener) {
        this.enumeratorId = enumeratorId;
        this.logger = Logger.getLogger(enumeratorId + "");
        this.listener = listener;
    }

    public void run() {
        while (true) {
            try {
                if (currCustomer != null && !currCustomer.isVisited()) {
                    logger.info(String.format("%s Start collecting data at %s,%s", enumeratorId,
                            currCustomer.getLocation().getLatitude(), currCustomer.getLocation().getLongitude()));

                    currCustomer.setVisited(true);
                    listener.publishVisit(enumeratorId, currCustomer.getId());
                    Thread.sleep(1000);

                    logger.info(String.format("%s Finish collecting data at %s,%s", enumeratorId,
                            currCustomer.getLocation().getLatitude(), currCustomer.getLocation().getLongitude()));

                    subscribing = listener.subscribeSolution(enumeratorId);
                } else {
                    if(!subscribing) {
                        subscribing = listener.subscribeSolution(enumeratorId);
                    }

                    Thread.sleep((long) 10000);
                }
            } catch (Exception e) {}
        }
    }

    public void publishCustomers(List<Customer> customers) {
        this.currCustomer = (TCustomer) customers.get(0);
    }
}
