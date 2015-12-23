package com.mio.jrdv.pushsenderjrdvsoft.parse;

import android.app.Application;

/**
 * Created by joseramondelgado on 08/12/15.
 */
public class MyApplicationParse extends Application {
    /*
    This is an Application class which will be executed on app launch.
    So we initialize the parse by calling ParseUtils.registerParse() method
    which initializes the parse and subscribe the user to AndroidHive channel.
     */


    private static MyApplicationParse mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // register with parse
        ParseUtils.registerParse(this);
    }


    public static synchronized MyApplicationParse getInstance() {
        return mInstance;
    }

}
