package com.hexleo.game.sniper.util;

import android.text.TextUtils;

import com.hexleo.game.sniper.app.BaseApplication;
import com.hexleo.game.sniper.log.SLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by hexleo on 2017/8/30.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static final String DATA_PATH = getDataPath() + "/";
    public static final String SCORE_E_DATA = DATA_PATH + "score_e.info";
    public static final String SCORE_N_DATA = DATA_PATH + "score_n.info";
    public static final String SCORE_H_DATA = DATA_PATH + "score_h.info";

    /**
     * @param fileName
     * @param obj
     */
    public static void writeObject(String fileName, Object obj) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            SLog.e(TAG, "FileUtils.writeObject throw an Exception. fileName=" + fileName + ", Exception=" + e.toString());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    SLog.e(TAG, e.toString());
                }
            }
        }
    }

    public static Object readObject(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bis);

            return ois.readObject();
        } catch (Exception e) {
            SLog.e(TAG, "FileUtils.readObject throw an Exception. fileName=" + fileName + ", Exception=" + e.toString());
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    SLog.e(TAG, e.toString());
                }
            }
        }

        return null;
    }

    public static String getDataPath() {
        try {
            File file = BaseApplication.getApp().getDir("info", 0);
            return file.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

}
