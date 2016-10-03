package com.soedomoto.bundle.se2016.controller;

import com.soedomoto.bundle.se2016.model.MFormL1;

import java.util.List;

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
