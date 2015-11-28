package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FetchService extends IntentService {
    public static final String LOG_TAG = "FetchService";
    final String FIXTURES = "fixtures";
    final String NEXT = "n";
    final String PREVIOUS = "p";

    public FetchService() {
        super("FetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(MainScreenFragment.ACTION_GETMATCHDATA)) {
            getGameData(NEXT + MainActivity.SPAN);
            getGameData(PREVIOUS + MainActivity.SPAN);
        }

        return;
    }

    private void getGameData(String timeFrame) {
        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();

        String JSON_data = Utilies.getJSONData(fetch_build);

        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray(FIXTURES);
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    Utilies.processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }

                Utilies.processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}

