package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

import items.Sight;
import utils.SightArrayAdapter;

/**
 * Created by Kyle on 1/20/2015.
 */
public class ManageActivity extends Activity
{
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

        adapter.add(new Sight("Test", new Geofence()
        {
            @Override
            public String getRequestId()
            {
                return null;
            }
        }));

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
}
