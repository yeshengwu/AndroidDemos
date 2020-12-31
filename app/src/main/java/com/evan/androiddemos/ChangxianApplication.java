package com.evan.androiddemos;

import android.app.Application;
import android.content.Context;

import com.bun.miitmdid.core.JLibrary;

public class ChangxianApplication extends Application {

    public static ChangxianApplication sInstance;

    public long deltaTimeProtocal = 0;
//    public long serverTimeProtocal = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

//        JLibrary.InitEntry(base);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

}
