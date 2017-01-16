package com.soedomoto.vrp.ws.api;

import com.soedomoto.vrp.ws.broker.SseAbstractBroker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soedomoto on 14/01/17.
 */
public class SsePubSub extends HttpServlet {
    private Map<Long, String> solutionCaches = new HashMap();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> headers = new HashMap();
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            String val = req.getHeader(key);
            headers.put(key, val);
        }

        if(headers.keySet().contains("Accept") && headers.get("Accept").equalsIgnoreCase("text/event-stream")) {
            if (headers.keySet().contains("enumerator-id")) {
                SseAbstractBroker broker = (SseAbstractBroker) req.getServletContext().getAttribute("broker");

                try {
                    broker.subscribe(headers.get("enumerator-id"), req, resp);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
