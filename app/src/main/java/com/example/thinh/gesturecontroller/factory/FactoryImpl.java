package com.example.thinh.gesturecontroller.factory;

import android.content.Context;

import com.example.thinh.gesturecontroller.application.GestureApplication;

public class FactoryImpl extends Factory{

    private GestureApplication mApplication;
    private Context mApplicationContext;

    public static Factory register(final Context applicationContext,
                                   final GestureApplication application) {
        final FactoryImpl factory = new FactoryImpl();
        Factory.setInstance(factory);
        sRegistered = true;
        factory.mApplication = application;
        factory.mApplicationContext = applicationContext;
        return factory;
    }
    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }
}
