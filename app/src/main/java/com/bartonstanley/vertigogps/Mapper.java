package com.bartonstanley.vertigogps;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by bartonstanley on 4/26/15.
 */
public class Mapper {

    private static final int INITIAL_ZOOM_LEVEL = 13;

    FragmentActivity mActivity;
    GoogleMap mMap = null;
    private static Mapper mMapper = null;
    private Marker mFenceMarker = null;
    private Marker mInitialMarker = null;

    private Mapper(FragmentActivity activity) {
        mActivity = activity;
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    public static Mapper getMapper(FragmentActivity activity) {

        // Get singleton
        if (mMapper == null) {
            mMapper = new Mapper(activity);
        }

        return mMapper;
    }

    public void mapInitialLocation(Location initialLocation) {

        // Use last known position unless it is null, whereupon use 0,0 instead just to have something
        LatLng markerPosition;
        if (initialLocation != null) {
            markerPosition = new LatLng(initialLocation.getLatitude(), initialLocation.getLongitude());
        }
        else {
            markerPosition = new LatLng(0, 0);
        }

        // Set up a green marker for the initial location.  If user taps marker he will see how far
        // away from it he is.  This is a simple way to give the user control over whether distance
        // is displayed or not.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(markerPosition);
        markerOptions.title("You are 0 miles from here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        // Add the marker and animate to it
        mInitialMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, INITIAL_ZOOM_LEVEL));
    }

    public void markTransitionOut(Location location) {

        // If there is no marker for this then add one
        if (mFenceMarker == null) {

            // Make it red
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            markerOptions.title("Here's where you hit one mile away");
            mFenceMarker = mMap.addMarker(markerOptions);
        }
        else {
            // If the marker was previously created then it is now hidden.  Update the position and
            // make it visible.
            if (!mFenceMarker.isVisible()) {
                mFenceMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                mFenceMarker.setVisible(true);
            }
        }
    }

    public void handleTransitionIn() {
        mFenceMarker.setVisible(false);
    }

    public void updateDistance(String distanceInMiles) {
        mInitialMarker.setTitle("You are " + distanceInMiles + " mi from here");

        // If window is shown hide and show it to refresh
        if (mInitialMarker.isInfoWindowShown()) {
            mInitialMarker.hideInfoWindow();
            mInitialMarker.showInfoWindow();
        }
    }
}
