package com.kwmuch.kyle.sitemap;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Kyle on 1/26/2015.
 */
public class NewSightActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener {

    private MapFragment mMapFragment = null;
    private GoogleMap mMap = null;
    private Location lastLocation = null;
    public GoogleApiClient mGoogleApiClient = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sight);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gMap);
        mMapFragment.getMapAsync(this);

        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation != null)
        {
            CameraPosition update = new CameraPosition.Builder()
                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .zoom( 17.5F )
                .bearing( 300F) // orientation
                .tilt( 50F) // viewing angle
                .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(update));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
