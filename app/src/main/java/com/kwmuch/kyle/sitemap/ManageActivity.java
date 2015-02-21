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
    public static final String PAR_KEY = "com.kwmuch.kyle.sightmap.spar";

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

        adapter.add(new Sight());
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

        Person mPerson = new Person();
        mPerson.setName("Leon");
        mPerson.setAge(25);
        Intent mIntent = new Intent(this,ObjectTranDemo1.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY,mPerson);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);

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
