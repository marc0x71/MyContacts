package com.marc0x71.mycontacts.di;

import android.content.Context;

import com.marc0x71.mycontacts.MyApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marc0x71 on 11/03/2016.
 */
@Module
public class AppModule {

    MyApp app;

    public AppModule(MyApp application) {
        app = application;
    }

    @Provides
    @Singleton
    MyApp providesApplication() {
        return app;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return app;
    }

    @Provides
    @Singleton
    int provideInteger() {
        return 123;
    }
}
