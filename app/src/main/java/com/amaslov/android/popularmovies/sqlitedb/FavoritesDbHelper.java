package com.amaslov.android.popularmovies.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String tableColumnID = FavoritesContract.FavoritesEntry._ID;
        String movieFullUrl = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL;
        String movieTitle = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
        String movieReleaseDate = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE;
        String movieVoteAverage = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE;
        String movieVoteCount = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT;
        String movieOverview = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW;

        String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                tableName + " (" +
                tableColumnID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                movieFullUrl + " TEXT NOT NULL," +
                movieTitle + " TEXT NOT NULL," +
                movieReleaseDate + " TEXT NOT NULL," +
                movieVoteAverage + " REAL NOT NULL," +
                movieVoteCount + " INTEGER NOT NULL," +
                movieOverview + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String SQL_DROP_FAVORITES_TABLE = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(SQL_DROP_FAVORITES_TABLE);
        onCreate(db);
    }
}
