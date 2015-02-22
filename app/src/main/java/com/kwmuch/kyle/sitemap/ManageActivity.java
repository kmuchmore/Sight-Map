package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

import items.Sight;
import items.SightDap;
import utils.SightArrayAdapter;

/**
 * Created by Kyle on 1/20/2015.
 */
public class ManageActivity extends Activity
{
    public static final int NEW_SIGHT_REQUEST = 1;
    public static final String PAR_KEY = "com.kwmuch.kyle.sightmap.spar";
    SightArrayAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<Sight> sightArrayList = (ArrayList<Sight>) SightDap.INSTANCE.getModel();
        adapter = new SightArrayAdapter(this, sightArrayList, R.layout.manage_sight_list_item);
        ListView sightListView = (ListView) findViewById(R.id.sightList2);
        sightListView.setAdapter(adapter);
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
        Sight sendSight = new Sight();
        Intent ns = new Intent(ManageActivity.this, NewSightActivity.class);
        Bundle sendBundle = new Bundle();
        sendBundle.putParcelable(PAR_KEY, sendSight);
        ns.putExtras(sendBundle);

        startActivityForResult(ns, NEW_SIGHT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_SIGHT_REQUEST) {
            Sight retSight = (Sight)data.getParcelableExtra(ManageActivity.PAR_KEY);
            Log.w("Locations", "Got sight back");
            int loc = -1;
            for (Sight s: SightDap.INSTANCE.getModel())
            {
                if(s.getmId() == retSight.getmId())
                {
                    loc = SightDap.INSTANCE.getModel().indexOf(s);
                    continue;
                }
            }
            if(loc != -1)
            {
                SightDap.INSTANCE.getModel().set(loc, retSight);
                SightDap.INSTANCE.updateFile();
            }
            else
            {
                SightDap.INSTANCE.getModel().add(retSight);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
