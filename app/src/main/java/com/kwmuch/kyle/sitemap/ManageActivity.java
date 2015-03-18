package com.kwmuch.kyle.sitemap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import items.Sight;
import items.SightDap;
import utils.SightArrayAdapter;

/**
 * Created by Kyle on 1/20/2015.
 */
public class ManageActivity extends Activity {
    public static final int NEW_SIGHT_REQUEST = 1;
    public static final String PAR_KEY = "com.kwmuch.kyle.sightmap.spar";
    SightArrayAdapter adapter = null;
    ArrayList<Sight> mDataList = null;
    ListView sightListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newSight(View v) {
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
            } else {
                for (int i = 0; i < SightDap.INSTANCE.getModel().size(); i++) {
                    boolean isSubPoly = true;
                    List<LatLng> compareFence = SightDap.INSTANCE.getModel().get(i).getmSiteFencePoly();
                    for (int j = 0; j < retSight.getmSiteFencePoly().size() && isSubPoly; j++) {
                        LatLng comparePoint = retSight.getmSiteFencePoly().get(j);
                        if (!PolyUtil.containsLocation(comparePoint, compareFence, false)) {
                            isSubPoly = false;
                        }
                    }
                    if (isSubPoly) {
                        loc = i;
                    }
                }
                if (loc != -1) {
                    subSightPopup(SightDap.INSTANCE.getModel().get(loc), retSight);
                    mDataList.add(loc, retSight);
                    sightListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    mDataList.add(retSight);
                    adapter.notifyDataSetChanged();
                }
                SightDap.INSTANCE.updateFile();
            }
        }
    }

    public void deleteSight(View view) {
        int rmSightIndex = ((Integer) view.getTag());
        Sight rmSight = adapter.getItem(rmSightIndex);

        deleteSightDialog(rmSight.getmSiteName(), view);
    }

    private void processDeleteSight(View view) {
        int rmSightIndex = ((Integer) view.getTag());
        Sight rmSight = adapter.getItem(rmSightIndex);

        File dir = new File(rmSight.getmFolderPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        if (!dir.delete()) {
            Log.w("Process", "Delete Failed");
        }

        adapter.remove(rmSight);
        sightListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        SightDap.INSTANCE.updateFile();
    }

    private void deleteSightDialog(String sightName, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("--BACKUP YOUR DATA--\nThis will delete all images associated with this sight");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processDeleteSight(view);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean isSubPolygon() {

        return false;
    }

    private void subSightPopup(Sight pSight, Sight cSight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice");
        builder.setMessage(cSight.getmSiteName() + " is a Sub-Sight of " + pSight.getmSiteName() + " and has been sorted by " + pSight.getmSiteName());

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
