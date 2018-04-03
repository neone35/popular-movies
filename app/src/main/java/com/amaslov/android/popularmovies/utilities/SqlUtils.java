package com.amaslov.android.popularmovies.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amaslov.android.popularmovies.sqlitedb.FavoritesContract;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesDbHelper;

public class SqlUtils {

    final private static String READABLE_DB = "readable";
    final private static String WRITABLE_DB = "writable";
    private static SQLiteDatabase mFavoritesDB;

    public SqlUtils() {
    }

    public static Cursor getFavoritesTable(Context context) {
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String columnID = FavoritesContract.FavoritesEntry._ID;
        mFavoritesDB = getFavoritesDB(READABLE_DB, context);
        return mFavoritesDB.query(favoritesTableName, null, null, null, null, null, columnID);
    }

    public static SQLiteDatabase getFavoritesDB(String action, Context context) {
        FavoritesDbHelper favoritesDbHelper = new FavoritesDbHelper(context);
        if (action.equals(READABLE_DB)) {
            mFavoritesDB = favoritesDbHelper.getReadableDatabase();
        } else if (action.equals(WRITABLE_DB)) {
            mFavoritesDB = favoritesDbHelper.getWritableDatabase();
        }
        return mFavoritesDB;
    }
}
