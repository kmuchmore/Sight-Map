package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import items.Sight;

/**
 * Created by Kyle on 1/26/2015.
 */
public class NewSightActivity extends FragmentActivity implements OnMapReadyCallback {

    MapFragment mMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sight);

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.newSightC, mMapFragment);
        fragmentTransaction.commit();

//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapy);
//        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
}
