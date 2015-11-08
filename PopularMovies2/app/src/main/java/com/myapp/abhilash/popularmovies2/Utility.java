package com.myapp.abhilash.popularmovies2;

import android.content.Context;
import android.database.Cursor;


class Utility {

    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildImageUrl(String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(342) + fileName;
    }
}
