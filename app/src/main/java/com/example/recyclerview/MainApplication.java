package com.example.recyclerview;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.clevertap.android.sdk.CleverTapAPI;

import com.clevertap.android.sdk.pushnotification.amp.CTPushAmpListener;



public class MainApplication extends Application implements CTPushAmpListener {

    private static Handler handler = null;
    private CleverTapAPI clevertap;
    public static boolean sCleverTapSegmentEnabled = false;
    @Override
    public void onCreate() {
        super.onCreate();
        // initialize Rudder SDK here
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
        cleverTapAPI.setCTPushAmpListener(this);
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
    }
    @Override
    public void onPushAmpPayloadReceived(Bundle bundle) {
        bundle.getChar("KEY");
        Log.d("Push Received","Push ");
    }
}
