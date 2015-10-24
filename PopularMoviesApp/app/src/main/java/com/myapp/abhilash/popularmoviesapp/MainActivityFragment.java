package com.myapp.abhilash.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;
    private final List<Movie> retrievedMovies= new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Set up custom ArrayAdapter with temp movie
        List<Movie> tempMovie = new ArrayList<>();
        //tempMovie.add(new Movie("test", "test", "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg", "test", "test"));

        movieAdapter = new MovieAdapter(getActivity(), tempMovie);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_gridview);
        gridView.setAdapter(movieAdapter);

        //Launch detail Activity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);

                //Pass movie info via extras
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("title", movie.getTitle());
                i.putExtra("releaseDate", movie.getRelease_date());
                i.putExtra("posterPath", movie.getPoster_path());
                i.putExtra("voteAverage", movie.getVote_average());
                i.putExtra("overview", movie.getOverview());
                startActivity(i);
            }
        });

        return rootView;
    }


    //Helper methods
    private void updateMovies() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    //Worker thread
    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        //Get JSON
        private List<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            //Names of JSON objects and arrays
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_OVERVIEW = "overview";

            final String TMDB_BASE_URL = "http://image.tmdb.org/t/p/";
            final String TMDB_IMAGE_SIZE = "w185/";

            JSONObject completeObject = new JSONObject(movieJsonStr);
            JSONArray resultsArray = completeObject.getJSONArray(TMDB_RESULTS);

            //String to hold all info for a movie
            //String[] resultStr = new String[resultsArray.length()];

            //Go through each item
            for (int i = 0 ; i < resultsArray.length() ; i++ )
            {
                String title;
                String release_date;
                String poster_path;
                String vote_average;
                String overview;

                //Find the current movie
                JSONObject currentMovie = resultsArray.getJSONObject(i);

                //Find each variable
                title = currentMovie.getString(TMDB_TITLE);
                release_date = currentMovie.getString(TMDB_RELEASE_DATE);
                poster_path = currentMovie.getString(TMDB_POSTER_PATH);
                vote_average = currentMovie.getString(TMDB_VOTE_AVERAGE);
                overview = currentMovie.getString(TMDB_OVERVIEW);

                retrievedMovies.add(new Movie(title, release_date, TMDB_BASE_URL + TMDB_IMAGE_SIZE + poster_path, vote_average, overview));

                //resultStr[i] = title + release_date + poster_path + vote_average + overview;
                //resultStr[i] = TMDB_BASE_URL + TMDB_IMAGE_SIZE + poster_path;
            }

            return retrievedMovies;
        }

        //Make connection and get JSON
        @Override
        protected List<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            //Get preference
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = pref.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

            //Need to clear List<Movies> because if sort preference is changed, movies must be changed too
            retrievedMovies.clear();

            //Values for URI
            String key = getString(R.string.api_key);

            try {
                //Base URL and keys for URI
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                //Build URI
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, sort)
                        .appendQueryParameter(API_KEY, key)
                        .build();

                URL url = new URL(builtUri.toString());

                //Create connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    //Nothing to do
                    movieJsonStr = null;
                }

                assert inputStream != null;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    //Steam was empty. No need to parse.
                    movieJsonStr = null;
                }

                movieJsonStr = buffer.toString();

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error", e);
                movieJsonStr = null;

            } finally {
                //Close connection
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e)
                    {
                        Log.e(LOG_TAG, "Error closing stream.", e);
                    }
                }
            }

            //Make call to get JSON
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            //Only occurs when there was is error getting or parsing the forecast
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {

                //Clear temp movies
                movieAdapter.clear();

                //Add each movie from JSON (automatically updates activity)
                for (Movie item : result) {
                    movieAdapter.add(item);
                }

            }
        }
    }
}
