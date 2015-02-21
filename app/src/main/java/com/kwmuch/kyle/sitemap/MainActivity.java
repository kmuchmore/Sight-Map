package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

import items.Sight;
import utils.SightArrayAdapter;


public class MainActivity extends Activity
{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int idCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Sight> sightArrayList = new ArrayList<Sight>();
        SightArrayAdapter adapter = new SightArrayAdapter(this, sightArrayList, R.layout.main_sight_list_item);

        ListView sightListView = (ListView) findViewById(R.id.sightList);
        sightListView.setAdapter(adapter);

        adapter.add(new Sight(1, "Test Worksite"));
        adapter.add(new Sight(2, "Test Worksite2"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }
    }

    private void openManageView()
    {
        startActivity(new Intent(MainActivity.this, ManageActivity.class));
    }

    private void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public static int getNewID()
    {
        idCount++;
        return idCount;
    }

    private void populateSightList()
    {

    }
}
