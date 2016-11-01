package com.soedomoto.validation.sakernas;

import com.soedomoto.validation.core.service.IValidation;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Created by soedomoto on 10/31/16.
 */
public class Activator implements BundleActivator {
    private ServiceRegistration registration;

    public void start(BundleContext context) throws Exception {
        registration = context.registerService(IValidation.class.getName(), new SakernasValidation(), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
