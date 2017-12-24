package ajar.huzefa.olaplay;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * <p>
 * This is a Singleton for the application that provides a Volley Request Queue as well as a Shared Preferences Object.
 */

public class OlaPlay {

    private static OlaPlay instance;
    private final Context context;
    public SharedPreferences data;
    private RequestQueue requestQueue;

    /**
     * @param context This context is used to extract the application context from the passed parameter to reduce memory leaks.
     *                The Application context is then used to initialize SharedPreferences and RequestQueue
     * @return Returns the singleton for OlaPlay
     */
    public static synchronized OlaPlay get(Context context) {
        return instance = instance == null ? new OlaPlay(context.getApplicationContext()) : instance;
    }

    private OlaPlay(Context context) {
        this.context = context;
        data = PreferenceManager.getDefaultSharedPreferences(this.context);
        requestQueue = Volley.newRequestQueue(this.context);
    }


    public void request(Request request) {
        requestQueue.add(request);
    }

    public static void log(Object object) {
        if (object != null)
            Log.v(OlaPlay.class.getSimpleName(), object.toString());
        else Log.v(OlaPlay.class.getSimpleName(), "" + object);
    }
}
