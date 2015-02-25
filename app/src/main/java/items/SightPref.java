package items;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Kyle on 1/26/2015.
 */

public class SightPref {

    private static final String PREFS_NAME = "SightPref";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static Gson gson;

    /**
     * Constructor takes an android.content.Context argument
     */
    public SightPref(Context ctx) {
        if (settings == null) {
            settings = ctx.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
        }
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        editor = settings.edit();
    }

    public void add(Sight sight)
    {
        String sight_json = obj2Gson(sight);
        // store in SharedPreferences
        String id =  "" +  sight.getmId(); // get storage key
        editor.putString(id, sight_json);
        editor.commit();
    }

    private String obj2Gson(Sight sight)
    {
        String sight_json = gson.toJson(sight);

        return sight_json;
    }

    public Sight get(int id)
    {
        // do the reverse operation
        String sight_json = settings.getString("" + id, "");
        Sight sight = gson.fromJson(sight_json, Sight.class);

        return sight;
    }

}