package items;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kwmuch.kyle.sitemap.NewSightActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2/22/2015.
 */
public enum SightDap
{
    INSTANCE;
    private static Context c;
    private final String fileDataName = "sightData";
    private List<Sight> contentProvider = null;

    private SightDap()
    {
        contentProvider = new ArrayList<Sight>();
    }

    public List<Sight> getModel()
    {
        return contentProvider;
    }

    public void init(Context context)
    {
        c = context.getApplicationContext();
        getInitialSightData();
    }

    private void getInitialSightData()
    {
        Gson gson = new Gson();
        try {
            BufferedReader br = new BufferedReader(new FileReader(c.getFilesDir() + "/" + fileDataName));
            contentProvider = gson.fromJson(new FileReader(c.getFilesDir() + "/" + fileDataName), new TypeToken<List<Sight>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w("Setup", "File not found");
            Sight unknownSight = new Sight("Unknown Sight");
            String dirName = NewSightActivity.getSightStorageDir(unknownSight.getmSiteName()).toString();
            unknownSight.setmFolderPath(dirName);
            SightDap.INSTANCE.getModel().add(unknownSight);
        }
    }

    public void updateFile()
    {
        Gson gson = new Gson();
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(c.getFilesDir() + "/" + fileDataName));
            String toFile = gson.toJson(contentProvider, new TypeToken<List<Sight>>(){}.getType());
            br.flush();
            br.write(toFile);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
