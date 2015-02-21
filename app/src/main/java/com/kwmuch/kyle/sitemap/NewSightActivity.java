package com.kwmuch.kyle.sitemap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import items.Sight;

/**
 * Created by Kyle on 1/26/2015.
 */
public class NewSightActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
//        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback{

    //Map Variables
    private MapFragment mMapFragment = null;
    private GoogleMap mMap = null;
    public GoogleApiClient mGoogleApiClient = null;

    //Location variables
    private Location mCurrentLocation = null;
    private LocationRequest mLocReq = null;
    boolean mIsReqLocUpdates = false;
    boolean addPoints = false;
    private String m_Text = "";

    //UI variables
    private EditText mSightName = null;
    private Button mAddGeoFenceButton = null;
    private Vector<LatLng> geoFencePolygonPoints = null;

    //@TODO Pass in Sight and have it return that... not sure how

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sight);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gMap);
        mMapFragment.getMapAsync(this);

        mSightName = (EditText) findViewById(R.id.sightName);
        mAddGeoFenceButton = (Button) findViewById(R.id.addGeoFence);
        geoFencePolygonPoints = new Vector<LatLng>();

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newsight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.goto_location:
                getAddress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
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

    public void saveSight(View view)
    {
        int resultCode = ManageActivity.NEW_SIGHT_REQUEST;
        Sight retSight = new Sight(MainActivity.getNewID(), m_Text);
        Parcel retParcel;
        retSight.writeToParcel(retParcel, );
        Intent retI = new Intent();
        retI.putExtra("retSight", retSight);
        setResult(RESULT_OK, retI);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w("Locations", "Location Updating");
        mCurrentLocation = location;
        updateMapLocation();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
        stopLocationUpdates();
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

//    @Override
//    public void onMapLongClick(LatLng latLng) {
//
//        String sightName = mSightName.getText().toString();
//
//        geoFencePolygonPoints.add(latLng);
//        Log.w("Locations", "Added GeoPoint to Vector\n");
//
//        mMap.clear();
//        drawPointsGeoFence();
//
//        if(geoFencePolygonPoints.size() >= 3)
//        {
//            drawPolygonGeoFence();
//        }
//    }

    private void drawPointsGeoFence() {
        Log.w("Locations", "Adding Points");
        for(LatLng point:geoFencePolygonPoints) {
            mMap.addMarker(new MarkerOptions()
                            .position(point)
            );
        }
    }

    private void gotoAddress() {
        Log.w("Locations", "Got here");
        if(!m_Text.isEmpty())
        {
            Geocoder gc = new Geocoder(this);
            List<Address> loc = null;
            try {
                loc = gc.getFromLocationName(m_Text, 1);
                mCurrentLocation.setLatitude(loc.get(0).getLatitude());
                mCurrentLocation.setLongitude(loc.get(0).getLongitude());
                updateMapLocation();
            } catch (IOException e) {
                Log.w("Locations", "No Location");
                e.printStackTrace();
            }
        }
    }

    private void getAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Address");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                gotoAddress();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = "";
                dialog.cancel();
            }
        });

        builder.show();
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

    public void clearMap(View view) {
        Log.w("Locations", "Clearing Map");
        mMap.clear();
        geoFencePolygonPoints.clear();
    }

    public void toggleAddGeoPoints(View view) {
        addPoints = !addPoints;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(addPoints)
        {
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
    }
}
