package com.soedomoto.bundle.jetty.service;

import org.eclipse.jetty.server.Handler;

/**
 * Created by soedomoto on 16/07/16.
 */
public interface ContextHandlerService {
    public void addHandler(Handler handler) throws Exception;
    public void removeHandler(Handler handler) throws Exception;
}
