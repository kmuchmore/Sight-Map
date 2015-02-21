package items;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Kyle on 1/20/2015.
 */
public class Sight implements Parcelable{
    private int id;
    private String mSiteName;
    private Geofence mSiteFence;
    private List<LatLng> mSiteFencePoly;
    private Date mLastUpdated;
    private String mFolderPath;
    private int mNumPics;

    public Sight(int id, String mSiteName) {
        this.id = id;
        this.mSiteName = mSiteName;
        this.mLastUpdated = new Date();
        this.mNumPics = 0;
    }

    public Sight(Parcel in) {
        this.id = in.readInt();
        this.mSiteName = in.readString();
        this.mSiteFence = (Geofence)in.readValue(Geofence.class.getClassLoader());
        LatLng[] tmpG = (LatLng[])in.readValue(LatLng[].class.getClassLoader());
        this.mSiteFencePoly = new Vector<LatLng>(Arrays.asList(tmpG));
        this.mLastUpdated = (Date)in.readValue(Date.class.getClassLoader());
        this.mFolderPath = in.readString();
        this.mNumPics = in.readInt();
    }

    public static final Parcelable.Creator<Sight> CREATOR
            = new Parcelable.Creator<Sight>() {
        public Sight createFromParcel(Parcel in) {
            return new Sight(in);
        }

        public Sight[] newArray(int size) {
            return new Sight[size];
        }
    };

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

    public List<LatLng> getmSiteFencePoly() {
        return mSiteFencePoly;
    }

    public void setmSiteFencePoly(List<LatLng> mSiteFencePoly) {
        this.mSiteFencePoly = mSiteFencePoly;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mSiteName);
        dest.writeValue(mSiteFence);
        dest.writeValue(mLastUpdated);
        dest.writeString(mFolderPath);
        dest.writeInt(mNumPics);
    }
}
