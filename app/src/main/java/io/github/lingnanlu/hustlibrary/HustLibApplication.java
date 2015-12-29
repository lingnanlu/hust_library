package io.github.lingnanlu.hustlibrary;

import android.app.Application;

import io.github.lingnanlu.core.AppAction;
import io.github.lingnanlu.core.AppActionImpl;

/**
 * Created by Administrator on 2015/12/29.
 */
public class HustLibApplication extends Application {

    private AppAction appAction;

    @Override
    public void onCreate() {
        super.onCreate();
        appAction = new AppActionImpl();
    }

    public AppAction getAppAction() {
        return appAction;
    }
}
