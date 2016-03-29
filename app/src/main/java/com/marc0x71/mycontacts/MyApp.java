package com.marc0x71.mycontacts;

import android.app.Application;

import com.marc0x71.mycontacts.di.AppComponent;
import com.marc0x71.mycontacts.di.AppModule;
import com.marc0x71.mycontacts.di.DaggerAppComponent;

/**
 * Created by marc0x71 on 11/03/2016.
 */
public class MyApp extends Application {

    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
