package com.soedomoto.bundle.se2016.test;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.bundle.se2016.controller.*;
import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.service.AccountHandlerService;
import com.soedomoto.bundle.se2016.service.DaoHandlerService;
import com.soedomoto.bundle.se2016.service.PropertyHandlerService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by soedomoto on 9/6/2016.
 */
public class Activator implements BundleActivator {
    private PropertyHandlerService _properties;
    private DaoHandlerService _dao;
    private AccountHandlerService _account;
    private String dataDir;
    private HttpClient _client;
    private Gson _gson;

    public void start(BundleContext context) throws Exception {
        //  Handle Storage
        String fwDir = context.getProperty("org.osgi.framework.storage");
        File dataFile = new File(fwDir + File.separator + "data" + File.separator +
                context.getBundle().getBundleId());
        dataFile.mkdirs();
        dataDir = dataFile.getAbsolutePath();

        //  Handle Properties
        ServiceReference pSR = context.getServiceReference(PropertyHandlerService.class.getName());
        _properties = (PropertyHandlerService) context.getService(pSR);

        //  Handle Dao
        ServiceReference dSR = context.getServiceReference(DaoHandlerService.class.getName());
        _dao = (DaoHandlerService) context.getService(dSR);

        //  Handle Account
        ServiceReference aSR = context.getServiceReference(AccountHandlerService.class.getName());
        _account = (AccountHandlerService) context.getService(aSR);

        _gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        _client = new HttpClient();
        _client.start();

        //  Test GET
        _remoteTest();
        _localTest();
    }

    private void _getWilcah(final String host, final String filename) throws Exception {
        _httpGet(host + _properties.getContextPath() + CWilayahCacah.WilayahCacah.PATH +
                "?pencacah=" + _account.getPencacahID(), new BufferingResponseListener() {
            public void onComplete(Result result) {
                _logRequest(filename, result);

                if(result.getResponse().getStatus() == 200) {
                    MWilayahCacah[] wilcahs = _gson.fromJson(getContentAsString(), MWilayahCacah[].class);
                    for (MWilayahCacah wilcah : wilcahs) {
                        try {
                            _getBS(host, filename, wilcah);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void _getBS(final String host, final String filename, final MWilayahCacah wilcah) throws Exception {
        _httpGet(host + _properties.getContextPath() + CBlokSensus.BlokSensusByKode.PATH +
                    "?fullKode=" + wilcah.getBlokSensus().getFullKode(),
            new BufferingResponseListener() {
                public void onComplete(Result result) {
                    _logRequest(filename, result);

                    if(result.getResponse().getStatus() == 200) {
                        MBlokSensus v105 = _gson.fromJson(getContentAsString(), MBlokSensus.class);
                        try {
                            _getKel(host, filename, v105);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    private void _getKel(final String host, final String filename, final MBlokSensus v105) throws Exception {
        _httpGet(host + _properties.getContextPath() + CKelurahan.KelurahanByKode.PATH +
                    "?fullKode=" + v105.getKelurahan().getFullKode(),
            new BufferingResponseListener() {
                public void onComplete(Result result) {
                    _logRequest(filename, result);

                    if(result.getResponse().getStatus() == 200) {
                        MKelurahan v104 = _gson.fromJson(getContentAsString(), MKelurahan.class);
                        try {
                            _getKec(host, filename, v104);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    private void _getKec(final String host, final String filename, final MKelurahan v104) throws Exception {
        _httpGet(host + _properties.getContextPath() + CKecamatan.KecamatanByKode.PATH +
                        "?fullKode=" + v104.getKecamatan().getFullKode(),
            new BufferingResponseListener() {
                public void onComplete(Result result) {
                    _logRequest(filename, result);

                    if(result.getResponse().getStatus() == 200) {
                        MKecamatan v103 = _gson.fromJson(getContentAsString(), MKecamatan.class);
                        try {
                            _getKab(host, filename, v103);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    private void _getKab(final String host, final String filename, final MKecamatan v103) throws Exception {
        _httpGet(host + _properties.getContextPath() + CKabupaten.KabupatenByKode.PATH +
                        "?fullKode=" + v103.getKabupaten().getFullKode(),
            new BufferingResponseListener() {
                public void onComplete(Result result) {
                    _logRequest(filename, result);

                    if(result.getResponse().getStatus() == 200) {
                        MKabupaten v102 = _gson.fromJson(getContentAsString(), MKabupaten.class);
                        try {
                            _getProp(host, filename, v102);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    private void _getProp(final String host, final String filename, final MKabupaten v102) throws Exception {
        _httpGet(host + _properties.getContextPath() + CPropinsi.PropinsiByKode.PATH +
                        "?fullKode=" + v102.getPropinsi().getFullKode(),
            new BufferingResponseListener() {
                public void onComplete(Result result) {
                    _logRequest(filename, result);

                    if(result.getResponse().getStatus() == 200) {
                        MPropinsi v101 = _gson.fromJson(getContentAsString(), MPropinsi.class);
                    }
                }
            });
    }

    private void _remoteTest() throws Exception {
        final String host = _properties.getRealHost();
        final String filename = "remote.csv";

        for(int i=0; i<1; i++) {
            _getWilcah(host, filename);
        }
    }

    private void _localTest() throws Exception {
        final String host = "http://localhost:5555";
        final String filename = "local.csv";

        for(int i=0; i<1; i++) {
            _getWilcah(host, filename);
        }
    }

    private void _httpGet(String url, BufferingResponseListener onComplete) throws Exception {
        _client.newRequest(url)
            .onRequestBegin(new Request.BeginListener() {
                public void onBegin(Request request) {
                    request.attribute("start", new Date().getTime());
                }
            })
            .onRequestFailure(new Request.FailureListener() {
                public void onFailure(Request request, Throwable failure) {
                    System.out.println(String.format("Failure %s", failure.toString()));
                }
            })
            .onResponseFailure(new Response.FailureListener() {
                public void onFailure(Response response, Throwable failure) {
                    System.out.println(String.format("Failure %s", failure.toString()));
                }
            })
            .send(onComplete);
    }

    private void _logRequest(String filename, Result result) {
        long start = Long.valueOf(String.valueOf(result.getRequest().getAttributes().get("start")));
        long responseTime = new Date().getTime() - start;
        _writeCSV(filename, new Date().toGMTString(), result.getRequest().getURI().toString(),
                String.valueOf(result.getResponse().getStatus()),
                result.getResponse().getHeaders().get("Content-Length"), String.valueOf(responseTime));
    }

    private void _writeCSV(String filename, String... cells) {
        try {
            CSVWriter _csv = new CSVWriter(new FileWriter(dataDir + File.separator + filename, true), ',');
            _csv.writeNext(cells);
            _csv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(BundleContext context) throws Exception {

    }
}
