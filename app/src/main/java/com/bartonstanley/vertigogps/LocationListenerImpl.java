package com.bartonstanley.vertigogps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;



/**
 * Created by bartonstanley on 4/26/15.
 */
public class LocationListenerImpl implements LocationListener {

    private static final int ONE_MILE_IN_METERS = 1609;
    private static final int DISTANCE_THRESHOLD = 50;

    private static final float METERS_TO_MILES_CONVERSION_FACTOR = 0.00062137f;  // Need to convert meters to miles
    private static final int MIN_LOCATION_TIME_DELTA = 5 * 1000;  // milliseconds
    private static final int NOTIFICATION_ID = 1; // Need ability to identify a notification

    private static LocationListenerImpl mListener = null; // Singleton

    private FragmentActivity mActivity;
    private Location mInitialLocation = null;
    private boolean mInsideFence = true;
    private LocationManager mLocationManager = null;
    private Mapper mMapper = null;
    private Notifier mNotifier = null;
    private boolean mVisible = false;


    private LocationListenerImpl(FragmentActivity activity) {
        mActivity = activity;

        // Get the LocationManager so we can do our job
        mLocationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);

        // Get the Notifier to handle the details of showing and hiding notification
        mNotifier = Notifier.getNotifier(activity);
    }

    public static LocationListenerImpl getLocationListener(FragmentActivity activity) {

        // Get singleton
        if (mListener == null) {
            mListener = new LocationListenerImpl(activity);
        }

        return mListener;
    }

    @Override
    public void onLocationChanged(Location location) {

        // Calculate distance away from initial location
        float[] result = new float[1];
        Location.distanceBetween(mInitialLocation.getLatitude(), mInitialLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), result);

        // Convert to miles and display in initial marker and on notification if it is there
        float miles = result[0] * METERS_TO_MILES_CONVERSION_FACTOR;
        String formattedDistanceInMiles = String.format("%.02f", miles);
        mMapper.updateDistance(formattedDistanceInMiles);
        if (!mInsideFence && !isVisible())
            mNotifier.updateDistance(NOTIFICATION_ID, formattedDistanceInMiles);

        // Are we more than one mile away?
        if (result[0] > DISTANCE_THRESHOLD) {

            // Were we less than one mile away last we knew?
            if (mInsideFence) {

                // Put up a red marker to show where we were one mile away
                mMapper.markTransitionOut(location);

                // If app is not visible, put up a notification
                if (!isVisible())
                    mNotifier.createNotification(NOTIFICATION_ID, formattedDistanceInMiles);

                // Now that we are beyond one mile don't do this again until we come back within one mile
                mInsideFence = false;
            }
        }
        else {  // We are less than a mile away
            if (!mInsideFence) {

                // If last we knew we were beyond one mile away then make the marker and notification disappear
                mMapper.handleTransitionIn();
                mNotifier.cancel(NOTIFICATION_ID);

                // Now that we are less than one mile away don't do this again until we go beyond one mile
                mInsideFence = true;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // do nothing for now
    }

    @Override
    public void onProviderEnabled(String provider) {
        // do nothing for now
    }

    @Override
    public void onProviderDisabled(String provider) {
        // do nothing for now
    }

    public void cancel() {
        mLocationManager.removeUpdates(this);
        mNotifier.cancelAll();
    }

    public void initMap() {
        mMapper = Mapper.getMapper(mActivity);
    }

    public void initLocation() {

        if (mInitialLocation != null)
            return;

        // Get a location as quickly as possible
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mInitialLocation = lastKnownLocation;

        // Put initial location on map
        mMapper.mapInitialLocation(lastKnownLocation);

        // Request location updates using GPS
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_LOCATION_TIME_DELTA, 0, this);

    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }
}
