package com.soedomoto.proxy;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.proxy.ProxyServlet;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by soedomoto on 06/11/16.
 */
public class ValidationProxy extends ProxyServlet {
    private final Integer serverPort;

    public ValidationProxy(Integer serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    protected void onResponseSuccess(HttpServletRequest request, HttpServletResponse response, Response proxyResponse) {
        StringBuffer uri = request.getRequestURL();
        String query = request.getQueryString();
        if (query != null)
            uri.append("?").append(query);

        System.out.println(String.format("=== Request %s proxied successfully ===", uri));
        super.onResponseSuccess(request, response, proxyResponse);
    }

    @Override
    protected void onResponseFailure(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, Throwable failure) {
        String query = request.getQueryString();

        StringBuffer uri = request.getRequestURL();
        if (query != null) uri.append("?").append(query);

        System.out.println(String.format("=== Request %s proxing failed ===", uri));

        // Redirection
        String redirectURL = String.format("http://127.0.0.1:%s%s%s", serverPort, request.getPathInfo(),
                                            query == null ? "" : "?" + query);

        System.out.println(String.format("=== Request %s redirecting to %s ===", uri, redirectURL));

        if(serverPort != null) {
            try {
                URL url = new URL(redirectURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (request.getMethod().equalsIgnoreCase("POST")) {
                    conn.setDoOutput(true);
                    conn.setChunkedStreamingMode(0);
                    conn.setRequestProperty("Content-Type", request.getHeader("Content-Type"));

                    OutputStreamWriter out= new OutputStreamWriter(conn.getOutputStream());
                    IOUtils.copy(new InputStreamReader(request.getInputStream()), out);
                    out.flush();
                }

                IOUtils.copyLarge(conn.getInputStream(), response.getOutputStream());
            } catch (MalformedURLException e) {
                if (!response.isCommitted()) response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                if (!response.isCommitted()) response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            AsyncContext asyncContext = (AsyncContext)request.getAttribute(ASYNC_CONTEXT);
            asyncContext.complete();
        } else {
            super.onResponseFailure(request, response, proxyResponse, failure);
        }
    }
}
