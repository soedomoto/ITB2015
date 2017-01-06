package com.soedomoto.optaplanner;

/**
 * Created by soedomoto on 03/01/17.
 */
public interface SubscribeListener {
    boolean subscribeSolution(Long enumeratorId);
    void publishVisit(Long enumeratorId, Long customerId);
}
