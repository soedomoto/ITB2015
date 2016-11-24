package com.soedomoto.pgfw7.util;

import com.soedomoto.pgfw7.ProxyApplication;

import org.apache.cordova.LOG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by soedomoto on 5/14/16.
 */
public class ClassUtil {
    private static final String TAG = ClassUtil.class.getSimpleName();

    public static String[] getAssetsList(String dirname) throws IOException {
        return ProxyApplication.getAppContext().getAssets().list(dirname);
    }

    public static File openAssetFile(String filename) throws IOException {
        InputStream inputDex = ProxyApplication.getAppContext().getAssets().open(filename);

        String[] fileComps = filename.split("/");
        File work = new File(ProxyApplication.getWorkingDir(), fileComps[fileComps.length-1]);

        FileOutputStream outputDex = new FileOutputStream(work);
        byte[] buf = new byte[0x1000];
        while (true) {
            int r = inputDex.read(buf);
            if (r == -1)
                break;
            outputDex.write(buf, 0, r);
        }
        outputDex.close();
        inputDex.close();

        return work;
    }

    public static Class[] getApplicationClasses() throws IOException {
        return getApplicationClasses("");
    }

    public static Class[] getApplicationClasses(String packageName) throws IOException {
        DexFile dx = new DexFile(ProxyApplication.getAppContext().getPackageCodePath());
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        return getClasses(dx, cl, packageName);
    }

    public static Class[] getDexClasses(File dexFile) throws IOException {
        return getDexClasses(dexFile, "");
    }

    public static Class[] getDexClasses(File dexFile, String packageName) throws IOException {
        DexFile dx = DexFile.loadDex(dexFile.getAbsolutePath(), File.createTempFile("opt", "dex",
                ProxyApplication.getAppContext().getCacheDir()).getPath(), 0);

        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        BaseDexClassLoader cl = new DexClassLoader(dexFile.getAbsolutePath(), ProxyApplication
                .getOptimizedDir(dexFile).getAbsolutePath(), null, parent);

        Thread.currentThread().setContextClassLoader(cl);

        return getClasses(dx, cl, packageName);
    }

    private static Class[] getClasses(DexFile dx, ClassLoader cl, String packageName) {
        List<Class> classes = new ArrayList<Class>();
        for(Enumeration<String> classNames = dx.entries(); classNames.hasMoreElements();) {
            String className = classNames.nextElement();
            try {
                Class<?> clazz = cl.loadClass(className);
                if(clazz.getPackage().getName().startsWith(packageName)) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.e(TAG, e.getLocalizedMessage());
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }

}
