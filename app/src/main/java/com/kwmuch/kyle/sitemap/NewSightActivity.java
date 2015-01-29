package com.kwmuch.kyle.sitemap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.GoogleMap.CancelableCallback;

/**
 * Created by Kyle on 1/26/2015.
 */
public class NewSightActivity extends FragmentActivity implements OnMapReadyCallback {

    MapFragment mMapFragment;
    static GoogleMap mMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sight);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gMap);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(mMap == null)
        {
            mMap = map;
        }
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        initCamera();
        toggleView();
    }

    public void initCamera() {
        CameraPosition INIT = new CameraPosition.Builder()
                .target(new LatLng(41.7378, -111.8308))
                .zoom( 17.5F )
                .bearing( 300F) // orientation
                .tilt( 50F) // viewing angle
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(INIT));
    }

    /**
     * Toggle View Satellite-Normal
     */
    public static void toggleView(){
        mMap.setMapType( mMap.getMapType() ==
                GoogleMap.MAP_TYPE_NORMAL ?
                GoogleMap.MAP_TYPE_SATELLITE :
                GoogleMap.MAP_TYPE_NORMAL);
    }

    private static CancelableCallback callback = new CancelableCallback() {
        @Override
        public void onFinish() {
            scroll();
        }
        @Override
        public void onCancel() {}
    };

    public static void scroll() {
        // we don't want to scroll too fast since
        // loading new areas in map takes time
        mMap.animateCamera( CameraUpdateFactory.scrollBy(10, -10),
                callback ); // 10 pix
    }
}
