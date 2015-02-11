package com.kwmuch.kyle.sitemap;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Vector;

import items.Sight;

/**
 * Created by Kyle on 1/26/2015.
 */
public class NewSightActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener,
        OnMapReadyCallback{

    private MapFragment mMapFragment = null;
    private GoogleMap mMap = null;
    private Location mCurrentLocation = null;
    public GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocReq = null;
    private EditText mSightName = null;
    boolean mIsReqLocUpdates = false;
    Sight mSight = null;
    private Vector<LatLng> geoFencePolygonPoints = null;

    //@TODO Pass in Sight and have it return that... not sure how

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sight);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gMap);
        mMapFragment.getMapAsync(this);

        mSightName = (EditText) findViewById(R.id.sightName);
        geoFencePolygonPoints = new Vector<LatLng>();

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mCurrentLocation != null)
        {
            CameraPosition update = new CameraPosition.Builder()
                .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(update));
        }

        if(!mIsReqLocUpdates)
        {
            startLocationUpdates();
        }

    }

    protected void createLocationRequest() {
        mLocReq = new LocationRequest();
//        mLocReq.setInterval(60000);
//        mLocReq.setFastestInterval(10000);
        mLocReq.setInterval(8000);
        mLocReq.setFastestInterval(5000);
        mLocReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocReq, this);
        mIsReqLocUpdates = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w("Locations", "Location Updating");
        mCurrentLocation = location;
        updateMapLocation();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mIsReqLocUpdates = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mIsReqLocUpdates) {
            startLocationUpdates();
        }
    }

    private void updateMapLocation() {
        CameraPosition update = new CameraPosition.Builder()
                .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                .zoom(13.9f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(update));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        String sightName = mSightName.getText().toString();

        geoFencePolygonPoints.add(latLng);
        Log.w("Locations", "Added GeoPoint to Vector\n");

        mMap.clear();
        drawPointsGeoFence();

        if(geoFencePolygonPoints.size() >= 3)
        {
            drawPolygonGeoFence();
        }
    }

    private void drawPointsGeoFence() {
        Log.w("Locations", "Adding Points");
        for(LatLng point:geoFencePolygonPoints) {
            mMap.addMarker(new MarkerOptions()
                            .position(point)
            );
        }
    }

    private void drawPolygonGeoFence() {
        Log.w("Locations", "Drawing polygon");
        mMap.addPolygon(new PolygonOptions()
            .addAll(geoFencePolygonPoints)
            .fillColor(0x40ff0000)
            .strokeColor(Color.TRANSPARENT)
            .strokeWidth(2)
        );
    }

    public void clearMap() {
        Log.w("Locations", "Clearing Map");
        mMap.clear();
        geoFencePolygonPoints.clear();
    }
}
