package com.soedomoto.sakernas;

import android.app.Application;
import android.util.Log;

import com.soedomoto.sakernas.osgi.KfOsgi;
import com.soedomoto.validation.core.service.IValidation;

import org.knopflerfish.framework.Main;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soedomoto on 10/30/16.
 */

public class ProxyApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    private KfOsgi kfOsgi;

    @Override
    public void onCreate() {
        super.onCreate();

        kfOsgi = new KfOsgi(getApplicationContext());

        // Install Required Bundle
        try {
            Bundle validationCore = kfOsgi.getFramework().getBundleContext().getBundle("validation-core.jar");
            if(validationCore == null || (validationCore != null && validationCore.getState() == Bundle.UNINSTALLED)) {
                validationCore = kfOsgi.getFramework().getBundleContext()
                        .installBundle("validation-core.jar", getAssets().open("validation-core.jar"));
            }

            Bundle sakernasValidation = kfOsgi.getFramework().getBundleContext().getBundle("sakernas-validation.jar");
            if(sakernasValidation == null || (sakernasValidation != null && sakernasValidation.getState() == Bundle.UNINSTALLED)) {
                sakernasValidation = kfOsgi.getFramework().getBundleContext()
                        .installBundle("sakernas-validation.jar", getAssets().open("sakernas-validation.jar"));
            }

            kfOsgi.start();

            ServiceReference ref = sakernasValidation.getBundleContext().getServiceReference(IValidation.class);
            IValidation ivalidation = (IValidation) sakernasValidation.getBundleContext().getService(ref);
//
//            Log.i(TAG, String.valueOf(ivalidation == null));
        } catch (BundleException e) {
            Log.e(TAG, String.format("Bundles is failed to installed"), e);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to access assets folder"), e);
        }
    }

    public KfOsgi getKfOsgi() {
        return kfOsgi;
    }
}
