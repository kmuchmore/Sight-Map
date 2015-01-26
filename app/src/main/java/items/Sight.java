package items;

import android.graphics.Bitmap;

import com.google.android.gms.location.Geofence;

import java.util.Date;

/**
 * Created by Kyle on 1/20/2015.
 */
public class Sight {
    private int id;
    private String mSiteName;
    private Geofence mSiteFence;
    private Date mLastUpdated;
    private String mFolderPath;
    private int mNumPics;

    public Sight(int id, String mSiteName, Geofence mSiteFence) {
        this.id = id;
        this.mSiteName = mSiteName;
        this.mSiteFence = mSiteFence;
        this.mLastUpdated = new Date();
        this.mNumPics = 0;
    }

    public int getId() {
        return id;
    }

    public void addPic(Bitmap image) {
        /* @TODO Figure out how to save image to card */

        mNumPics++;
        mLastUpdated = new Date();
    }

    @Override
    public String toString() {
        return mSiteName;
    }

    public String getmSiteName() {
        return mSiteName;
    }

    public void setmSiteName(String mSiteName) {
        this.mSiteName = mSiteName;
    }

    public Geofence getmSiteFence() {
        return mSiteFence;
    }

    public void setmSiteFence(Geofence mSiteFence) {
        this.mSiteFence = mSiteFence;
    }

    public Date getmLastUpdated() {
        return mLastUpdated;
    }

    public String getmFolderPath() {
        return mFolderPath;
    }

    public void setmFolderPath(String mFolderPath) {
        this.mFolderPath = mFolderPath;
    }

    public int getmNumPics() {
        return mNumPics;
    }
}
