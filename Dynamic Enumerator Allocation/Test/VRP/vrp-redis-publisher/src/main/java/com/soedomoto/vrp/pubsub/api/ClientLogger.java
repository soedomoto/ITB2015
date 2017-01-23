package com.soedomoto.vrp.pubsub.api;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by soedomoto on 09/01/17.
 */
@Path("/log")
public class ClientLogger {
    @Context
    ServletContext context;

    @POST
    @Path("/{enumerator}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response log(@PathParam("enumerator") String enumeratorId, String msg) throws IOException {
        String clientLogDir = (String) context.getAttribute("clientLogDir");

        File clientLogFile = new File(clientLogDir + File.separator + enumeratorId + ".log");
        clientLogFile.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(new FileWriter(clientLogFile, true));
        out.println(msg);
        out.flush();

        return Response.ok(String.format("%s -> %s : %s", enumeratorId, clientLogDir, msg)).build();
    }

}
