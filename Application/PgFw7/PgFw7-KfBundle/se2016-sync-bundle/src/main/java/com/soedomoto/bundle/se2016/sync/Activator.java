package com.soedomoto.bundle.se2016.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.bundle.se2016.controller.CSync;
import com.soedomoto.bundle.se2016.service.AccountHandlerService;
import com.soedomoto.bundle.se2016.service.DaoHandlerService;
import com.soedomoto.bundle.se2016.service.PropertyHandlerService;
import com.soedomoto.bundle.se2016.tools.IpChecker;
import org.eclipse.jetty.client.HttpClient;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Created by soedomoto on 02/09/16.
 */
public class Activator implements BundleActivator {
    public static final Integer SYNC_INTERVAL = 10000;

    private PropertyHandlerService _properties;
    private DaoHandlerService _dao;
    private ThreadGroup _threads;
    private AccountHandlerService _account;

    public void start(BundleContext context) throws Exception {
        //  Handle Properties
        ServiceReference pSR = context.getServiceReference(PropertyHandlerService.class.getName());
        _properties = (PropertyHandlerService) context.getService(pSR);

        //  Handle Dao
        ServiceReference dSR = context.getServiceReference(DaoHandlerService.class.getName());
        _dao = (DaoHandlerService) context.getService(dSR);

        //  Handle Account
        ServiceReference aSR = context.getServiceReference(AccountHandlerService.class.getName());
        _account = (AccountHandlerService) context.getService(aSR);

        //  Initiate Http Client
        HttpClient client = new HttpClient();
        client.start();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        //  Initiate Threads
        _threads = new ThreadGroup(context.getBundle().getSymbolicName());
        _threads.setDaemon(true);

        try {
            String remoteIPAddr = InetAddress.getByName(new URI(_properties.getRealHost()).getHost()).getHostAddress();
            String currentIPAddr = IpChecker.getIp();

            System.out.println(String.format("Current IP : %s", currentIPAddr));
            System.out.println(String.format("Remote IP : %s", remoteIPAddr));

            if(! currentIPAddr.equalsIgnoreCase(remoteIPAddr)) {
                Thread syncMaster = new Thread(_threads, new SyncMaster(SYNC_INTERVAL, _properties, _dao, _account,
                        client, gson));
                syncMaster.setDaemon(true);
                syncMaster.start();

                Thread syncData = new Thread(_threads, new SyncData(SYNC_INTERVAL, _properties, _dao, _account,
                        client, gson));
                syncData.setDaemon(true);
//                syncData.start();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(BundleContext context) throws Exception {
        _threads.stop();
    }
}
