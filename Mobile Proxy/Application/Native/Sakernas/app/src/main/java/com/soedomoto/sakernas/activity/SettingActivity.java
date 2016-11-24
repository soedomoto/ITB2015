package com.soedomoto.sakernas.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.soedomoto.sakernas.ProxyApplication;
import com.soedomoto.sakernas.R;

import org.osgi.framework.BundleException;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public ProxyApplication getProxyApplication() {
        return (ProxyApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Start Stop OSGI Framework
        final ToggleButton osgiFrameworkBtn = (ToggleButton) findViewById(R.id.osgiFrameworkBtn);
        osgiFrameworkBtn.setChecked(getProxyApplication().getKnopflerfish().isStarted());

        osgiFrameworkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new AsyncTask<Void, Void, Exception>() {
                        @Override
                        protected Exception doInBackground(Void... params) {
                            Exception ex = null;

                            try {
                                getProxyApplication().getKnopflerfish().start();
                            } catch (BundleException e) {
                                ex = e;
                            }

                            return ex;
                        }

                        @Override
                        protected void onPostExecute(Exception e) {
                            if(e == null) {
                                Log.i(TAG, String.format("OSGi Framework is started"));
                            } else {
                                Log.e(TAG, String.format("OSGi Framework is failed to start"), e);
                                osgiFrameworkBtn.setChecked(false);
                            }
                        }
                    }.execute();
                } else {
                    new AsyncTask<Void, Void, Exception>() {
                        @Override
                        protected Exception doInBackground(Void... params) {
                            Exception ex = null;

                            try {
                                getProxyApplication().getKnopflerfish().start();
                            } catch (BundleException e) {
                                ex = e;
                            }

                            return ex;
                        }

                        @Override
                        protected void onPostExecute(Exception e) {
                            if(e == null) {
                                Log.i(TAG, String.format("OSGi Framework is stopped"));
                            } else {
                                Log.e(TAG, String.format("OSGi Framework is failed to stop"), e);
                                osgiFrameworkBtn.setChecked(true);
                            }
                        }
                    }.execute();
                }
            }
        });

        // Install Bundle
        final EditText bundleURL = (EditText) findViewById(R.id.bundleURL);
        Button installBundleBtn = (Button) findViewById(R.id.installBundleBtn);

        installBundleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, Exception>() {
                    @Override
                    protected Exception doInBackground(String... urls) {
                        Exception ex = null;

                        try {
                            org.osgi.framework.Bundle bundle = getProxyApplication().getKnopflerfish().installBundle(urls[0]);
                            bundle.start();
                        } catch (BundleException e) {
                            ex = e;
                        }

                        return ex;
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        if(e == null) {
                            Log.i(TAG, String.format("Bundle %s is started", bundleURL.getText().toString()));
                        } else {
                            Log.e(TAG, String.format("Bundle %s is failed to start", bundleURL.getText().toString()), e);
                        }
                    }
                }.execute(bundleURL.getText().toString());
            }
        });
    }

}
