package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import items.Sight;
import items.SightDap;
import utils.SightArrayAdapter;


public class MainActivity extends Activity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_TAKE_PHOTO = 1;
    static int idCount = 0;
    static int image_count_before;
    static Integer tempLocHolder = null;
    public GoogleApiClient mGoogleApiClient = null;
    boolean mIsReqLocUpdates = false;
    private Location mCurrentLocation = null;
    private LocationRequest mLocReq = null;
    SightArrayAdapter adapter = null;
    ContentObserver co;

    public Cursor loadCursor() {

        final String[] columns = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID };

        final String orderBy = MediaStore.Images.Media.DATE_ADDED;

        return getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
    }

    private AdapterView.OnItemClickListener mItemClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Log.w("Process", "Position: " + position + " id: " + id);
            takePicture(position);
        }
    };

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
        adapter = new SightArrayAdapter(this, R.layout.main_sight_list_item, (ArrayList<Sight>) sightArrayList);

        ListView sightListView = (ListView) findViewById(R.id.sightList);
        sightListView.setAdapter(adapter);
        sightListView.setOnItemClickListener(mItemClickedHandler);

        buildGoogleApiClient();
        createLocationRequest();
        updateFileNums();
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
                openSettingView();
                break;
            case R.id.manage_site:
                openManageView();
                break;
            case R.id.capture_image:




                takePicture(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w("Process", "Request Code: " + requestCode);
        Log.w("Process", "Result Code: " + resultCode);

        Log.w("Process", "LocHolder: " + tempLocHolder);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            Log.w("Process", "Took image");

            exitingCamera(tempLocHolder);

            tempLocHolder = null;

            updateFileNums();
            adapter.notifyDataSetChanged();
            SightDap.INSTANCE.updateFile();
        }
        else
        {
            Log.w("Process", "Something failed");
        }
    }

    private void openManageView() {
        startActivity(new Intent(MainActivity.this, ManageActivity.class));
    }

    private void openSettingView() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

//    MediaStore.ACTION_IMAGE_CAPTURE
    private void takePicture(Integer position) {
        Cursor cursor = loadCursor();
        image_count_before = cursor.getCount();
        cursor.close();

        tempLocHolder = position;

        Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//
//            File photoFile = null;
//            try {
//                photoFile = prepForImageCapture(position);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if(photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
    }

    public void viewImages(View view) {
        int viewSightNum = ((Integer) view.getTag());
        Sight viewSight = adapter.getItem(viewSightNum);

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivity(galleryIntent);

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        File dirFile = new File(viewSight.getmFolderPath());
//        File dirFile = new File("/storage/emulated/0/Pictures/Sight Map/Unknown Sight/*");
//        Log.w("Process", dirFile.getAbsolutePath());
//        intent.setDataAndType(Uri.parse(dirFile.getAbsolutePath()), "image/*");
//        intent.setType("images/*");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    public String[] getImagePaths(Cursor cursor, int startPosition) {

        int size = cursor.getCount() - startPosition;

        if (size <= 0)
            return null;

        String[] paths = new String[size];

        int dataColumnIndex = cursor
                .getColumnIndex(MediaStore.Images.Media.DATA);

        for (int i = startPosition; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);

            paths[i - startPosition] = cursor.getString(dataColumnIndex);
        }

        return paths;
    }

    private void exitingCamera(Integer pos) {

        Cursor cursor = loadCursor();
        String[] paths = getImagePaths(cursor, image_count_before);
        cursor.close();

        if(paths != null) {
            processNewImages(paths, pos);
        }
    }

    private void processNewImages(String[] paths, Integer pos){
        Sight activeSight = getActiveSight(pos);

        for (String path : paths) {
            File currFile = new File(path);
            String newImageName = createImageFileName(activeSight);
            File endFile = new File(newImageName);
            moveFile(currFile, endFile);

            Uri contentUri = Uri.fromFile(endFile);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
            sendBroadcast(mediaScanIntent);

            Uri contentUri2 = Uri.fromFile(currFile);
            Intent mediaScanIntent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri2);
            sendBroadcast(mediaScanIntent2);
        }
    }

    private void moveFile(File source, File target){
        // your sd card
        String sdCard = Environment.getExternalStorageDirectory().toString();

        // the file to be moved or copied
//        File sourceLocation = new File (sdCard + "/sample.txt");

        // make sure your target location folder exists!
//        File targetLocation = new File (sdCard + "/MyNewFolder/sample.txt");

        // just to take note of the location sources
        Log.w("Process", "sourceLocation: " + source);
        Log.w("Process", "targetLocation: " + target);

        try {

            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;

            switch(actionChoice)
            {
                case 1:
                    if(source.renameTo(target)){
                        Log.w("Process", "Move file successful.");
                    }else{
                        Log.w("Process", "Move file failed.");
                    }
                    break;
                case 2:
                    FileChannel inChannel = new FileInputStream(source).getChannel();
                    FileChannel outChannel = new FileOutputStream(target).getChannel();

                    inChannel.transferTo(0, inChannel.size(), outChannel);

                    inChannel.close();
                    outChannel.close();

                    if(source.delete())
                    {
                        Log.w("Process", "Delete Succeeded");
                    }
                    else
                    {
                        Log.w("Process", "Delete Failed");
                    }
                    break;
                default:
                    break;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Sight getActiveSight(Integer position) {
        Sight newImageSight = null;
        if(mCurrentLocation != null)
        {
            if(position == null)
            {
                LatLng ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                for (Sight s : SightDap.INSTANCE.getModel()) {
                    if (PolyUtil.containsLocation(ll, s.getmSiteFencePoly(), false)) {
                        newImageSight = s;
                        break;
                    }
                }
            }
            else
            {
                newImageSight = SightDap.INSTANCE.getModel().get(position);
            }
            if(newImageSight == null)
            {
                newImageSight = SightDap.INSTANCE.getModel().get(0);
            }
            String imageSight = newImageSight.getmSiteName();
        }
        else {
            newImageSight = SightDap.INSTANCE.getModel().get(0);
        }
        return newImageSight;
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
        updateFileNums();
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
        Log.w("Setup", "Connection was disrupted");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w("Setup", "Connection has failed");
    }

    private String createImageFileName(Sight s) {
        String fileName = s.getmSiteName();
        Date today = Calendar.getInstance().getTime();

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean addDate = getPrefs.getBoolean("filename_date_checkbox", true);

        if(addDate && !isSameDay(DateToCalendar(today), DateToCalendar(s.getmLastUpdated()))) {
            s.setmIttVal(1);
        }
        fileName = fileName + String.format("_%03d", s.getmIttVal());

        if(addDate) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            fileName = fileName + "_" +sdf.format(Calendar.getInstance().getTime()) + ".jpg";
            int index = SightDap.INSTANCE.getModel().indexOf(s);
            SightDap.INSTANCE.getModel().get(index).setmLastUpdated(Calendar.getInstance().getTime());
        }
        else {
            fileName = fileName + ".jpg";
            int index = SightDap.INSTANCE.getModel().indexOf(s);
            SightDap.INSTANCE.getModel().get(index).setmLastUpdated(Calendar.getInstance().getTime());
         }

        File fileDir = new File(s.getmFolderPath());
        File fileOut = new File(fileDir, fileName);

        return fileOut.getAbsolutePath();
    }

//    private File prepForImageCapture(Integer position) throws IOException {
//        Sight newImageSight = null;
//        LatLng ll = null;
//        if(mCurrentLocation != null)
//        {
//            if(position == null)
//            {
//                ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                for (Sight s : SightDap.INSTANCE.getModel()) {
//                    if (PolyUtil.containsLocation(ll, s.getmSiteFencePoly(), false)) {
//                        newImageSight = s;
//                        break;
//                    }
//                }
//            }
//            else
//            {
//                newImageSight = SightDap.INSTANCE.getModel().get(position);
//            }
//            if(newImageSight == null)
//            {
//                newImageSight = SightDap.INSTANCE.getModel().get(0);
//            }
//            String imageSight = newImageSight.getmSiteName();
//        }
//        else {
//            newImageSight = SightDap.INSTANCE.getModel().get(0);
//            String imageSight = newImageSight.getmSiteName();
//        }
//
//        File imageFile = createImageFile(newImageSight);
//        Log.w("Process", "New Image Filepath: " + imageFile.getAbsolutePath());
//        return imageFile;
//    }

    private void updateFileNums() {
        for(Sight s : SightDap.INSTANCE.getModel()) {
            File dirFile = new File(s.getmFolderPath());
            if(!dirFile.exists())
            {
                dirFile.mkdirs();
            }
            s.setmNumPics(dirFile.list().length);
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static Calendar DateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void testClick(View view) {
        int loc = view.getId();
        Log.w("Process", "Test" + loc);
    }
}
