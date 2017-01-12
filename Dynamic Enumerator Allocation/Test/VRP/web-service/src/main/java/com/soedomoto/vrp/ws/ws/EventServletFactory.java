package com.soedomoto.vrp.ws.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.websocket.server.PathParam;
import java.io.IOException;

/**
 * Created by soedomoto on 10/01/17.
 */
@WebSocket
public class EventServletFactory {

    @OnWebSocketConnect
    public void onConnect(Session session, @PathParam("username") String userName) throws IOException {
        System.out.println(session.getRemoteAddress().getHostName() + " connected!");
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {
        System.out.println("Message received:" + message);
        if (session.isOpen()) {
            String response = message.toUpperCase();
            session.getRemote().sendString(response);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        System.out.println(session.getRemoteAddress().getHostName() + " closed!");
    }

}
