package io.recar.once2go.easynavi;

import android.app.Application;

import io.recar.once2go.usbserial.carwrapper.CarMediaCommunicationManager;

/**
 * Created by once2go on 07.04.16.
 */
public class EasyNaviApplication extends Application {

    private CarMediaCommunicationManager mCarMediaCommunicationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mCarMediaCommunicationManager = new CarMediaCommunicationManager(this);
    }

}
