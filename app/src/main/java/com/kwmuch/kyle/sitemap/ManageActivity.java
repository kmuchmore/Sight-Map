package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
    ArrayList<Sight> mDataList = null;
    ListView sightListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        getActionBar().setDisplayHomeAsUpEnabled(true);

//        dataList = SightDap.INSTANCE.getModel();
        mDataList = (ArrayList<Sight>) SightDap.INSTANCE.getModel();
        adapter = new SightArrayAdapter(this, R.layout.manage_sight_list_item, mDataList);
        sightListView = (ListView) findViewById(R.id.sightList2);
        sightListView.setAdapter(adapter);
//        adapter.fil
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == NEW_SIGHT_REQUEST) {
            Sight retSight = (Sight) data.getParcelableExtra(ManageActivity.PAR_KEY);
            Log.w("Locations", "Got sight back");
            int loc = -1;
            for (Sight s : mDataList) {
                if (s.getmId() == retSight.getmId()) {
                    loc = mDataList.indexOf(s);
                    continue;
                }
            }
            if (loc != -1) {
                mDataList.set(loc, retSight);
                SightDap.INSTANCE.updateFile();
            }
            else {
                mDataList.add(retSight);
                adapter.notifyDataSetChanged();
                SightDap.INSTANCE.updateFile();
            }
        }
    }

    public void deleteSight(View view)
    {
        int rmSight = ((Integer) view.getTag());
        adapter.remove(adapter.getItem(rmSight));
        sightListView.setAdapter(adapter);
//        adapter.

        adapter.notifyDataSetChanged();
        SightDap.INSTANCE.updateFile();
    }
}
