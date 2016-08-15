package com.bartonstanley.vertigogps;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by bartonstanley on 4/26/15.
 */
public class GapiConnectivityListener
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FragmentActivity mActivity;
    private static GapiConnectivityListener mListener = null;

    private GapiConnectivityListener(FragmentActivity activity) {
        mActivity = activity;
    }

    public static GapiConnectivityListener getGapiConnectivityListener(FragmentActivity activity) {
        if (mListener == null) {
            mListener = new GapiConnectivityListener(activity);
        }

        return mListener;
    }

    @Override
    public void onConnected(Bundle bundle) {

        // We are connected so now we can get our initial location
        LocationListenerImpl locationListener = LocationListenerImpl.getLocationListener(mActivity);
        locationListener.initLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnectionSuspended(int x) {
    }
}
