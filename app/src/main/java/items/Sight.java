package items;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kwmuch.kyle.sitemap.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Kyle on 1/20/2015.
 */
public class Sight implements Parcelable{
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mSiteName;
    @SerializedName("fence")
    private List<LatLng> mSiteFencePoly;
    @SerializedName("updated")
    private Date mLastUpdated;
    @SerializedName("path")
    private String mFolderPath;
    @SerializedName("num")
    private int mNumPics;

    public Sight() {
        this.mId = MainActivity.getNewID();
        this.mSiteName = "Sight Name";
        mSiteFencePoly = new ArrayList<LatLng>();
        this.mLastUpdated = Calendar.getInstance().getTime();
        this.mFolderPath = null;
        this.mNumPics = 0;
    }

    public Sight(int id) {
        this.mId = id;
        this.mSiteName = "Sight Name";
        mSiteFencePoly = new ArrayList<LatLng>();
        this.mLastUpdated = Calendar.getInstance().getTime();
        this.mFolderPath = null;
        this.mNumPics = 0;
    }

    public Sight(String mSiteName) {
        this.mId = MainActivity.getNewID();
        this.mSiteName = mSiteName;
        this.mSiteFencePoly = new ArrayList<LatLng>();
        this.mLastUpdated = Calendar.getInstance().getTime();
        this.mFolderPath = null;
        this.mNumPics = 0;
    }

    public Sight(Parcel in) {
        this.mId = in.readInt();
        this.mSiteName = in.readString();
        this.mSiteFencePoly = new ArrayList<LatLng>();
        in.readList(this.mSiteFencePoly, LatLng.class.getClassLoader());
        this.mLastUpdated = new Date(in.readLong());
        this.mFolderPath = in.readString();
        this.mNumPics = in.readInt();
    }

    public static final Parcelable.Creator<Sight> CREATOR = new Creator<Sight>() {
        public Sight createFromParcel(Parcel source) {
            Sight newSight = new Sight(0);
            newSight.mId = source.readInt();
            newSight.mSiteName = source.readString();

            newSight.mSiteFencePoly = new ArrayList<LatLng>();
            source.readList(newSight.mSiteFencePoly, LatLng.class.getClassLoader());

            newSight.mLastUpdated = new Date(source.readLong());
            newSight.mFolderPath = source.readString();
            newSight.mNumPics = source.readInt();
            return newSight;
        }
        public Sight[] newArray(int size) {
            return new Sight[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mSiteName);

        dest.writeList(mSiteFencePoly);

        dest.writeLong(mLastUpdated.getTime());
        dest.writeString(mFolderPath);
        dest.writeInt(mNumPics);
    }

    public void addPic(Bitmap image) {
        /* @TODO Figure out how to save image to card */

        mNumPics++;
        mLastUpdated = new Date();
    }

    public boolean isPointInPolygon(LatLng tap) {
        ArrayList<LatLng> vertices = (ArrayList<LatLng>) this.getmSiteFencePoly();
        int intersectCount = 0;
        for(int j=0; j<vertices.size()-1; j++) {
            if( rayCastIntersect(tap, vertices.get(j), vertices.get(j+1)) ) {
                intersectCount++;
            }
        }

        return (intersectCount % 2) == 1; // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
            return false; // a and b can't both be above or below pt.y, and a or b must be east of pt.x
        }

        double m = (aY-bY) / (aX-bX);               // Rise over run
        double bee = (-aX) * m + aY;                // y = mx + b
        double x = (pY - bee) / m;                  // algebra is neat!

        return x > pX;
    }

    @Override
    public String toString() {
        return mSiteName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int mId)
    {
        this.mId = mId;
    }

    public String getmSiteName()
    {
        return mSiteName;
    }

    public void setmSiteName(String mSiteName)
    {
        this.mSiteName = mSiteName;
    }

    public List<LatLng> getmSiteFencePoly()
    {
        return mSiteFencePoly;
    }

    public void setmSiteFencePoly(List<LatLng> mSiteFencePoly)
    {
        this.mSiteFencePoly = mSiteFencePoly;
    }

    public Date getmLastUpdated()
    {
        return mLastUpdated;
    }

    public void setmLastUpdated(Date mLastUpdated)
    {
        this.mLastUpdated = mLastUpdated;
    }

    public String getmFolderPath()
    {
        return mFolderPath;
    }

    public void setmFolderPath(String mFolderPath)
    {
        this.mFolderPath = mFolderPath;
    }

    public int getmNumPics()
    {
        return mNumPics;
    }

    public void setmNumPics(int mNumPics)
    {
        this.mNumPics = mNumPics;
    }
}
