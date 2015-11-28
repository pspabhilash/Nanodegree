package barqsoft.footballscores;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.database.DatabaseContract;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {
    public static final int SERIE_A = 401;
    public static final int PREMIER_LEGAUE = 398;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 399;
    public static final int BUNDESLIGA = 394;

    public static String getLeague(int league_num) {
        switch (league_num) {
            case SERIE_A:
                return "Seria A";
            case PREMIER_LEGAUE:
                return "Premier League";
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case BUNDESLIGA:
                return "Bundesliga";
            default:
                return "Not known League Please report";
        }
    }

    public static String getMatchDay(int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return "Group Stages, Matchday : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if (match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.ic_launcher;
        }
        switch (teamname) {
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.ic_launcher;
        }
    }

    public static final long SECONDS_IN_A_DAY = 86400000;

    // Returns a LONG type date
    public static long normalizeDate(long date) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(date);
        int julianDay = Time.getJulianDay(date, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    // Returns a STRING "2015-07-18"
    public static String getFragmentDate(int offSet) {
        Date fragmentdate = new Date(System.currentTimeMillis() + (offSet * SECONDS_IN_A_DAY));
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(fragmentdate);
    }

    // Returns a Long value from current date
    public static long getLongDateWithOffset(int offSet) {
        return System.currentTimeMillis() + (offSet * SECONDS_IN_A_DAY);
    }

    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();

        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getMatchDate(String dateString) {

        String matchDate = dateString.substring(0, dateString.indexOf("T"));
        String matchTime = dateString.substring(dateString.indexOf("T") + 1, dateString.indexOf("Z"));

        SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date parseddate = match_date.parse(matchDate + matchTime);
            SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            new_date.setTimeZone(TimeZone.getDefault());
            matchDate = new_date.format(parseddate);
            matchDate = matchDate.substring(0, matchDate.indexOf(":"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchDate;
    }

    public static String getMatchTime(String dateString) {

        String matchTime  = dateString.substring(dateString.indexOf("T") + 1, dateString.indexOf("Z"));
        String matchDate = dateString.substring(0, dateString.indexOf("T"));
        SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date parseddate = match_date.parse(matchDate + matchTime);
            SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
            new_date.setTimeZone(TimeZone.getDefault());
            matchDate = new_date.format(parseddate);
            matchTime = matchDate.substring(matchDate.indexOf(":") + 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchTime;
    }

    public static String getJSONData(Uri uri) {

        final String LOG_TAG = "GET SCORE DATA";

        //Log.v(LOG_TAG, fetch_build.toString()); //log spam

        Log.i(LOG_TAG, uri.toString()); //log spam

        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(uri.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token", "e136b7858d424b9da07c88f28b61989a");
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            Log.i(LOG_TAG, inputStream.toString()); //log spam

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JSON_data = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception here" + e.getMessage());
        } finally {
            if (m_connection != null) {
                m_connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
        return JSON_data;
    }

    public static void processJSONdata(String JSONdata, Context mContext, boolean isReal) {

        final String LOG_TAG = "PROCESS SCORE DATA";

        //JSON data
        final String SERIE_A = "401";
        final String PREMIER_LEGAUE = "398";
        final String CHAMPIONS_LEAGUE = "362";
        final String PRIMERA_DIVISION = "399";
        final String BUNDESLIGA = "395";
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String TEAM_LINK = "http://api.football-data.org/alpha/teams/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String HOMETEAM_ID = "homeTeam";
        final String AWAYTEAM_ID = "awayTeam";
        final String MATCH_DAY = "matchday";
        final String HREF = "href";
        final String EMPTY = "";

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);
            Log.i("INFO", "NO of Matches:" + matches.length());

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<ContentValues>(matches.length());
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match_data = matches.getJSONObject(i);
                Log.i("INFO", match_data.toString());
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString(HREF);
                League = League.replace(SEASON_LINK, EMPTY);

                if (League.equals(PREMIER_LEGAUE) ||
                        League.equals(SERIE_A) ||
                        League.equals(CHAMPIONS_LEAGUE) ||
                        League.equals(BUNDESLIGA) ||
                        League.equals(PRIMERA_DIVISION)) {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString(HREF);
                    match_id = match_id.replace(MATCH_LINK, EMPTY);

                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id = match_id + Integer.toString(i);
                    }

                    String matchString = match_data.getString(MATCH_DATE);

                    mDate = Utilies.getMatchDate(matchString);
                    mTime = Utilies.getMatchTime(matchString);

                    if (!isReal) {
                        //This if statement changes the dummy data's date to match our current date range.
                        mDate = Utilies.getFragmentDate(i - 2);
                    }

                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID, match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL, Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL, Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL, League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY, match_day);
                    //log spam

                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,mTime);
                    //Log.v(LOG_TAG,Home);
                    //Log.v(LOG_TAG,Away);
                    //Log.v(LOG_TAG,Home_goals);
                    //Log.v(LOG_TAG,Away_goals);

                    values.add(match_values);
                }
            }

            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

            Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
