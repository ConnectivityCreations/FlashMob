package com.stridera.connectivitycreations.flashmobApp.models;

import android.content.Context;

/**
 * Base application for accessing the data store.  Separation of data and android logic.
 */
public class FlashmobApplication extends com.activeandroid.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        FlashmobApplication.context = this;
    }


}
