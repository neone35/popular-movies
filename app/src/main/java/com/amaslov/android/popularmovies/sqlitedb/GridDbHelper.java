package com.amaslov.android.popularmovies.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GridDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "grid.db";
    public static final int DATABASE_VERSION = 1;

    public GridDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    ;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_GRID;
        String movieId = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID;
        String movieFullUrl = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL;

        String SQL_CREATE_GRID_TABLE = "CREATE TABLE " +
                tableName + " (" +
                movieId + " INTEGER NOT NULL," +
                movieFullUrl + "TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_GRID_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tableName = FavoritesContract.FavoritesEntry.TABLE_NAME_GRID;
        String SQL_DROP_GRID_TABLE = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(SQL_DROP_GRID_TABLE);
        onCreate(db);
    }
}
