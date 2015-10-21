package com.myapp.abhilash.mypopularmovies;

import android.content.Context;
import android.database.Cursor;

import com.myapp.abhilash.mypopularmovies.data.MovieContent;

public class Utility {

    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContent.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContent.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
