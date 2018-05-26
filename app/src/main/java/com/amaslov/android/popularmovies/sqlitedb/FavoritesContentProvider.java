package com.amaslov.android.popularmovies.sqlitedb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amaslov.android.popularmovies.utilities.SqlUtils;

public class FavoritesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDbHelper favoritesDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = FavoritesContract.AUTHORITY;
        String pathFavorites = FavoritesContract.PATH_FAVORITES;

        uriMatcher.addURI(authority, pathFavorites, FAVORITES);
        uriMatcher.addURI(authority, pathFavorites + "/#", FAVORITE_ITEM_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        favoritesDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor queryOutput;

        switch (match) {
            case FAVORITES:
                queryOutput = db.query(
                        SqlUtils.FAVORITES_TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                if (queryOutput == null) {
                    throw new android.database.SQLException("Failed to retrieve data from " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            queryOutput.setNotificationUri(getContext().getContentResolver(), uri);
        return queryOutput;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long newRowId = db.insert(
                        SqlUtils.FAVORITES_TABLE_NAME,
                        null,
                        values);
                if (newRowId > 0) {
                    returnUri = SqlUtils.FAVORITES_CONTENT_URI.buildUpon().appendPath(newRowId + "").build();
                } else {
                    throw new SQLException("Failed to insert movie" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        int deleteNum;

        switch (match) {
            case FAVORITES:
                deleteNum = db.delete(
                        SqlUtils.FAVORITES_TABLE_NAME,
                        selection,
                        selectionArgs);
                if (deleteNum <= 0) {
                    throw new android.database.SQLException("Failed to delete movie" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return deleteNum;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
