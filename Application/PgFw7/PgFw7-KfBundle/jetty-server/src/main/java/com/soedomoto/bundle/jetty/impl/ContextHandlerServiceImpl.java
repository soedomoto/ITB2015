package com.soedomoto.bundle.jetty.impl;

import com.soedomoto.bundle.jetty.service.ContextHandlerService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;

/**
 * Created by soedomoto on 16/07/16.
 */
public class ContextHandlerServiceImpl implements ContextHandlerService {
    private HandlerCollection _handlers;

    public ContextHandlerServiceImpl(HandlerCollection handlers) {
        _handlers = handlers;
    }

    public void addHandler(Handler handler) throws Exception {
        _handlers.addHandler(handler);
        handler.start();
    }

    public void removeHandler(Handler handler) throws Exception {
        handler.stop();
        _handlers.removeHandler(handler);
    }
}
