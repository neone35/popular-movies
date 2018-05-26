package com.amaslov.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.sqlitedb.FavoritesContract;

import static android.support.constraint.Constraints.TAG;

public class SqlUtils {

    final public static Uri FAVORITES_CONTENT_URI = FavoritesContract.FavoritesEntry.CONTENT_URI;
    final public static String FAVORITES_TABLE_NAME = FavoritesContract.FavoritesEntry.TABLE_NAME_MOVIE_FAVORITES;
    final public static String FAVORITES_ID = FavoritesContract.FavoritesEntry._ID;
    final public static String FAVORITES_FULL_URL = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_FULL_URL;
    final public static String FAVORITES_TITLE = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE;
    final public static String FAVORITES_RELEASE_DATE = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE;
    final public static String FAVORITES_AVERGAGE_VOTE = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE;

    public SqlUtils() {
    }

    public static boolean removeFromFavorites(String movieID, Context context) {
        String selectionID = SqlUtils.FAVORITES_ID + "=?";
        int rowsDeleted = context.getContentResolver().delete(FAVORITES_CONTENT_URI, selectionID, new String[]{movieID});
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
        ContentValues detailsValues = new ContentValues();
        detailsValues.put(FAVORITES_ID, movieId);
        detailsValues.put(FAVORITES_FULL_URL, movieDetails.getMoviePosterUrl());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE, movieDetails.getTitle());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, movieDetails.getMovieReleaseDate());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieDetails.getVoteAverage());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_VOTE_COUNT, movieDetails.getVoteCount());
        detailsValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_OVERVIEW, movieDetails.getOverview());
        Uri insertedUri = context.getContentResolver().insert(FAVORITES_CONTENT_URI, detailsValues);
        Log.d(TAG, "favorite inserted to: " + insertedUri);
        return true;
    }
}
