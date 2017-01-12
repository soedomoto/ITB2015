package com.soedomoto.vrp.ws.ws;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

/**
 * Created by soedomoto on 10/01/17.
 */
@WebServlet(urlPatterns="/events")
public class EventServlet extends WebSocketServlet {
    public void configure(WebSocketServletFactory factory) {
        factory.register(EventServletFactory.class);
    }
}
