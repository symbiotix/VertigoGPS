package com.bartonstanley.vertigogps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends FragmentActivity {

    private GapiConnectivityListener mGapiConnectivityListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // The location listener will keep track of location and detect the one-mile boundary
        LocationListenerImpl locationManager = LocationListenerImpl.getLocationListener(this);
        locationManager.initMap();

        // Keep in portrait to save development time dealing with screen orientation change :)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // This keeps track of Google API connectivity state.  Mostly is used to know when we
        // can start using the map
        mGapiConnectivityListener = GapiConnectivityListener.getGapiConnectivityListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mGapiConnectivityListener)
                .addOnConnectionFailedListener(mGapiConnectivityListener)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onPause() {
        super.onPause();

        // If paused then consider activity not visible
        LocationListenerImpl.getLocationListener(this).setVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If resumed consider activity visible
        LocationListenerImpl.getLocationListener(this).setVisible(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Google recommends connecting in onStart
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // We are stopping so disconnect Google API client and stop using GPS
        mGoogleApiClient.disconnect();
        LocationListenerImpl.getLocationListener(this).cancel();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}
