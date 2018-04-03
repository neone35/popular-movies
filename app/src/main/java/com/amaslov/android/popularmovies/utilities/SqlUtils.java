package com.amaslov.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesContract;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesDbHelper;

public class SqlUtils {

    private static SQLiteDatabase mFavoritesDB;
    final public static String READABLE_DB = "readable";
    final public static String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
    final public static String columnMovieID = FavoritesContract.FavoritesEntry._ID;
    final private static String WRITABLE_DB = "writable";

    public SqlUtils() {
    }

    public static Cursor getFavoritesTable(Context context) {
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String columnID = FavoritesContract.FavoritesEntry._ID;
        mFavoritesDB = getFavoritesDbAs(READABLE_DB, context);
        return mFavoritesDB.query(favoritesTableName, null, null, null, null, null, columnID);
    }

    public static SQLiteDatabase getFavoritesDbAs(String action, Context context) {
        FavoritesDbHelper favoritesDbHelper = new FavoritesDbHelper(context);
        if (action.equals(READABLE_DB)) {
            mFavoritesDB = favoritesDbHelper.getReadableDatabase();
        } else if (action.equals(WRITABLE_DB)) {
            mFavoritesDB = favoritesDbHelper.getWritableDatabase();
        }
        return mFavoritesDB;
    }

    public static boolean removeFromFavorites(String movieID, Context context) {
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        String columnMovieID = FavoritesContract.FavoritesEntry._ID;
        mFavoritesDB = SqlUtils.getFavoritesDbAs(WRITABLE_DB, context);
        int rowsDeleted = mFavoritesDB.delete(favoritesTableName, columnMovieID + "=" + movieID, null);
        if (rowsDeleted == 1) {
            return true;
        } else if (rowsDeleted > 1) {
            // show additional toast informing about multiple values removed
            showToast("More than one movie removed from favorites!", context);
            return true;
        } else {
            showToast("ERROR: Movie not removed from favorites", context);
            return false;
        }
    }

    private static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean addToFavorites(MovieDetails movieDetails, String movieId, Context context) {
        // Check if movie details fields are not empty
        String FAVORITES_ERROR = "Can't add to favorites.";
        String[] detailsFields = movieDetails.getAllDetails();
        String[] fieldErrors = {"Poster URL is empty. ", "Movie title is empty. ", "Movie release date is empty. ",
                "Movie vote average is empty. ", "Movie vote count is empty. ", "Movie overview is empty. "};
        for (int i = 0; i < detailsFields.length; i++) {
            if (detailsFields[i].isEmpty()) {
                showToast(fieldErrors[i] + FAVORITES_ERROR, context);
                return false;
            }
        }

        // Insert into favorites table otherwise
        String favoritesTableName = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
        ContentValues detailsValues = new ContentValues();
        detailsValues.put(FavoritesContract.FavoritesEntry._ID, movieId);
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL, movieDetails.getMoviePosterUrl());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, movieDetails.getTitle());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, movieDetails.getMovieReleaseDate());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieDetails.getVoteAverage());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT, movieDetails.getVoteCount());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW, movieDetails.getOverview());
        mFavoritesDB = SqlUtils.getFavoritesDbAs(WRITABLE_DB, context);
        mFavoritesDB.insert(favoritesTableName, null, detailsValues);
        return true;
    }
}
