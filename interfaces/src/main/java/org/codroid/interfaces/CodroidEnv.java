package org.codroid.interfaces;

import android.content.Context;

import org.codroid.interfaces.log.Loggable;
import org.codroid.interfaces.log.Logger;

import java.io.File;

public abstract class CodroidEnv implements Loggable {

    private Context context;
    public static String ADDONS_DIR_NAME = "addons";
    public static String LOG_FILE_DIR = "logs";
    public static String PRIVATE_DIR = "private";

    private String identify;
    private Logger logger;

    public CodroidEnv(Context context) {
        this.context = context;
    }

    public CodroidEnv() {

    }

    public void createCodroidEnv(CodroidEnv codroidEnv){
        initContext(codroidEnv.getContext());
    }

    protected void initContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected File getPrivateDir() {
        return getCodroidExternalDir(PRIVATE_DIR + "/" + getIdentify());
    }

    protected File getLogsDir() {
        return getCodroidExternalDir(LOG_FILE_DIR);
    }

    protected File getAddonsDir() {
        return getCodroidExternalDir(ADDONS_DIR_NAME);
    }

    protected File getCodroidExternalDir(String subDir) {
        return context.getExternalFilesDir(subDir);
    }

    public String setIdentify(String id) {
        return identify;
    }

    public String getIdentify() {
        return identify;
    }

    @Override
    public Logger getLogger() {
        if (logger == null) logger = new Logger(context);
        return logger.with(getIdentify());
    }
}
