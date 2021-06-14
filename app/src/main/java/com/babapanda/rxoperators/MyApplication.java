package com.babapanda.rxoperators;

import androidx.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;
    private RefWatcher refWatcher;

    public static MyApplication get() {
        return instance;
    }

    public static RefWatcher getRefWatcher() {
        return MyApplication.get().refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        instance = (MyApplication) getApplicationContext();
        refWatcher = LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
    }
}


