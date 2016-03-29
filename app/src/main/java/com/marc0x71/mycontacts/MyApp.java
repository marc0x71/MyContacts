package com.marc0x71.mycontacts;

import android.app.Application;
import android.util.Log;

import com.marc0x71.mycontacts.di.AppComponent;
import com.marc0x71.mycontacts.di.AppModule;
import com.marc0x71.mycontacts.di.DaggerAppComponent;

import timber.log.Timber;

/**
 * Created by marc0x71 on 11/03/2016.
 */
public class MyApp extends Application {

    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            Timber.plant(new ReleaseTree());
        }

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private static class ReleaseTree extends Timber.Tree {
        private static final int MAX_LOG_LENGTH = 4000;

        @Override
        protected boolean isLoggable(int priority) {
            return !(priority == Log.VERBOSE || priority == Log.DEBUG);
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (!isLoggable(priority)) return;

            if (priority == Log.ASSERT) {
                Log.wtf(tag, message);
            } else {
                Log.println(priority, tag, message);
            }
            return;

        }
    }
}
