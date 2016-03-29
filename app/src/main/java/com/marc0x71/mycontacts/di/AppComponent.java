package com.marc0x71.mycontacts.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marc0x71 on 11/03/2016.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Context provideContext();

}
