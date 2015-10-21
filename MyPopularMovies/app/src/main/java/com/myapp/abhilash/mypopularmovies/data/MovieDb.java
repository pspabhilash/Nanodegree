package com.myapp.abhilash.mypopularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieDb extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContent.MovieEntry.TABLE_NAME + " (" +
                MovieContent.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContent.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContent.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContent.MovieEntry.COLUMN_IMAGE + " TEXT, " +
                MovieContent.MovieEntry.COLUMN_IMAGE2 + " TEXT, " +
                MovieContent.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContent.MovieEntry.COLUMN_RATING + " INTEGER, " +
                MovieContent.MovieEntry.COLUMN_DATE + " TEXT);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContent.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
