package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

import items.Sight;
import utils.SightArrayAdapter;

/**
 * Created by Kyle on 1/20/2015.
 */
public class ManageActivity extends Activity
{
    public static final int NEW_SIGHT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<Sight> sightArrayList = new ArrayList<Sight>();
        SightArrayAdapter adapter = new SightArrayAdapter(this, sightArrayList, R.layout.manage_sight_list_item);

        ListView sightListView = (ListView) findViewById(R.id.sightList2);
        sightListView.setAdapter(adapter);

        adapter.add(new Sight(1, "Test"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newSight(View v)
    {
        Intent ns = new Intent(ManageActivity.this, NewSightActivity.class);
        startActivityForResult(ns, NEW_SIGHT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_SIGHT_REQUEST) {
            if (data.hasExtra("retSight")) {
                Sight newSight = data.getExtras().getParcelable("retSight");
            }
        }
    }
}
