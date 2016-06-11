package com.soedomoto.pgfw7.util;

import android.os.Environment;
import android.text.TextUtils;

import com.soedomoto.pgfw7.ProxyApplication;

import org.apache.cordova.LOG;

import java.io.File;

/**
 * Created by soedomoto on 5/20/16.
 */
public class StorageUtil {
    private static String TAG = StorageUtil.class.getSimpleName();

    public static File getExternalFilesDir(String... paths) {
        String appFilesPath = ProxyApplication.getAppContext().getExternalFilesDir(null)
                .getAbsolutePath();
        String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appFiles = appFilesPath.replace(extPath, "");

        boolean isInternalSdCard = !Environment.isExternalStorageRemovable();
        if(isInternalSdCard) {
            extPath = System.getenv("SECONDARY_STORAGE");
        }

        File extFile = null;
        if (!TextUtils.isEmpty(extPath)) {
            if (extPath.contains(":")) {
                for (String _path : extPath.split(":")) {
                    File file = new File(_path);
                    if (file.exists()) {
                        extFile = file;
                    }
                }
            } else {
                File file = new File(extPath);
                if (file.exists()) {
                    extFile = file;
                }
            }
        }

        if(extFile != null) {
            File extFilesPath = new File(extFile.getAbsolutePath() + appFiles);
            if (! extFilesPath.exists()) {
                extFilesPath.mkdirs();
            }

            return extFilesPath;
        }

        if (paths != null && paths.length > 0) {
            for (String _path : paths) {
                File file = new File(_path);
                if (file.exists()) {
                    return file;
                }
            }
        }
        return null;
    }

}
