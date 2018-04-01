package com.amaslov.android.popularmovies.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DetailsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "details.db";
    public static final int DATABASE_VERSION = 1;

    public DetailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    ;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_DETAILS;
        String movieTitle = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
        String movieReleaseDate = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE;
        String movieVoteAverage = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE;
        String movieVoteCount = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT;
        String movieOverview = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW;

        String SQL_CREATE_DETAILS_TABLE = "CREATE TABLE " +
                tableName + " (" +
                movieTitle + " TEXT NOT NULL," +
                movieReleaseDate + "TEXT NOT NULL," +
                movieVoteAverage + "TEXT NOT NULL," +
                movieVoteCount + "TEXT NOT NULL," +
                movieOverview + "TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_DETAILS;
        String SQL_DROP_DETAILS_TABLE = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(SQL_DROP_DETAILS_TABLE);
        onCreate(db);
    }

}
