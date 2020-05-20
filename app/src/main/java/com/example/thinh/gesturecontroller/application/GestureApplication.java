package com.example.thinh.gesturecontroller.application;

import android.app.Application;

import com.example.thinh.gesturecontroller.factory.FactoryImpl;

public class GestureApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createFactory();
    }

    //Bkav QuangNDb create factory cua ung dung
    protected void createFactory(){
        FactoryImpl.register(getApplicationContext(), this);
    }
}
