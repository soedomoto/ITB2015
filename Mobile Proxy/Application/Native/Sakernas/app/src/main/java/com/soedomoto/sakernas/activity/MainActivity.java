package com.soedomoto.sakernas.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soedomoto.sakernas.ProxyApplication;
import com.soedomoto.sakernas.R;
import com.soedomoto.sakernas.model.Ruta;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();


    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

    public ProxyApplication getProxyApplication() {
        return (ProxyApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Form
        final EditText txtPropinsi = (EditText) findViewById(R.id.txtPropinsi);
        final EditText txtKabupaten = (EditText) findViewById(R.id.txtKabupaten);
        final EditText txtKecamatan = (EditText) findViewById(R.id.txtKecamatan);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ruta ak = new Ruta();

                ak.setB1r1(txtPropinsi.getText().toString());
                ak.setB1r2(txtKabupaten.getText().toString());
                ak.setB1r3(txtKecamatan.getText().toString());

                String jsonEntry = gson.toJson(ak);
                Log.i(TAG, jsonEntry);

                //  Handle Validation
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... jsonEntries) {
                        String response = null;
                        String error = null;

                        try {
                            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", getProxyApplication().getProxyPort()));

                            URL url = new URL("http://192.168.1.16:9999/validate");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);

                            try {
                                conn.setDoOutput(true);
                                conn.setChunkedStreamingMode(0);
                                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                                out.write(jsonEntries[0]);
                                out.flush();

                                InputStream in = new BufferedInputStream(conn.getInputStream());
                                response = IOUtils.toString(in, Charset.defaultCharset());
                            } finally {
                                conn.disconnect();
                            }
                        } catch (MalformedURLException e) {
                            Log.e(TAG, e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                        return response;
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        Log.i(TAG, String.format("Response : %s", response));
                    }
                }.execute(jsonEntry);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
