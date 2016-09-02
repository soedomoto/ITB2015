package com.soedomoto.bundle.se2016.controller;

import com.soedomoto.bundle.se2016.model.*;
import com.soedomoto.bundle.se2016.tools.IpChecker;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.soedomoto.bundle.se2016.Activator.*;
import static com.soedomoto.bundle.se2016.controller.CBlokSensus.v105Dao;
import static com.soedomoto.bundle.se2016.controller.CFormL1.*;
import static com.soedomoto.bundle.se2016.controller.CKabupaten.v102Dao;
import static com.soedomoto.bundle.se2016.controller.CKecamatan.v103Dao;
import static com.soedomoto.bundle.se2016.controller.CKelurahan.v104Dao;
import static com.soedomoto.bundle.se2016.controller.CNks.v107Dao;
import static com.soedomoto.bundle.se2016.controller.CPencacah.pencacahDao;
import static com.soedomoto.bundle.se2016.controller.CPropinsi.v101Dao;
import static com.soedomoto.bundle.se2016.controller.CSls.v108Dao;
import static com.soedomoto.bundle.se2016.controller.CWilayahCacah.wilayahCacahDao;

/**
 * Created by soedomoto on 30/08/16.
 */
public class CSync {
    public static class SyncFormL1Response {
        private List<MFormL1> formL1s;
        private int changes;

        public SyncFormL1Response() {}

        public SyncFormL1Response(List<MFormL1> formL1s, int changes) {
            this.setFormL1s(formL1s);
            this.setChanges(changes);
        }

        public List<MFormL1> getFormL1s() {
            return formL1s;
        }

        public int getChanges() {
            return changes;
        }

        public void setFormL1s(List<MFormL1> formL1s) {
            this.formL1s = formL1s;
        }

        public void setChanges(int changes) {
            this.changes = changes;
        }
    }
}
