package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import items.Sight;
import items.SightDap;
import utils.SightArrayAdapter;


public class MainActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int idCount = 0;
    public GoogleApiClient mGoogleApiClient = null;
    boolean mIsReqLocUpdates = false;
    private Location mCurrentLocation = null;
    private LocationRequest mLocReq = null;

    public static int getNewID() {
        idCount++;
        return idCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SightDap.INSTANCE.init(this);
        List<Sight> sightArrayList = SightDap.INSTANCE.getModel();
        SightArrayAdapter adapter = new SightArrayAdapter(this, R.layout.main_sight_list_item, (ArrayList<Sight>) sightArrayList);

        ListView sightListView = (ListView) findViewById(R.id.sightList);
        sightListView.setAdapter(adapter);

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.manage_site:
                openManageView();
                break;
            case R.id.capture_image:
                takePicture();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String imageSight = "Unknown";
            String fileName = "error.jpg";
            LatLng ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            for (Sight s : SightDap.INSTANCE.getModel()) {
                if (PolyUtil.containsLocation(ll, s.getmSiteFencePoly(), false)) {
                    imageSight = s.getmSiteName();
                    fileName = s.getmNumPics() + ".";
                    continue;
                }
            }
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            File writeFile = getSightStoragePath(imageSight, fileName, true);
            Log.w("Process", "New Image Filepath: " + writeFile.getAbsolutePath());

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(writeFile);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openManageView() {
        startActivity(new Intent(MainActivity.this, ManageActivity.class));
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w("Locations", "Location Updating");
        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
//        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (!mIsReqLocUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getSightStoragePath(String sightName, String fileName, boolean addDate) {
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SightMap/" + sightName;
//        String dirPath = "/mnt/shared/sdcard/SightMap/" + sightName;
        if(addDate) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            fileName = fileName + sdf.format(Calendar.getInstance().getTime()) + ".jpg";
        }
        else {
            fileName = fileName + ".jpg";
        }

        File file = new File(dirPath, "test/");
        if(!isExternalStorageWritable()) {
            Log.w("Setup", "SD Card is not writable");
        }
        if (!file.exists()) {
            try {
                if(!file.createNewFile())
                    Log.w("Setup", "Could not create path to " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
