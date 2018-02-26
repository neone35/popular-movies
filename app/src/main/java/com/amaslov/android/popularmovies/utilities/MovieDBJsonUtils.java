package com.amaslov.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by aarta on 2018-02-24.
 */

public class MovieDBJsonUtils {

    public static final String MOVIE_DB_POSTER_PATH = "poster_path";
    public static final String MOVIE_DB_ID = "id";

    public static String getImageUrl(String configJson)
            throws JSONException {

        final String CONFIG_IMAGES_KEY = "images";
        final String CONFIG_BASE_URL_KEY = "secure_base_url";
        final String CONFIG_POSTER_KEY = "poster_sizes";
        final String CONFIG_POSTER_SIZE = "w185";

        JSONObject configJSONObject = new JSONObject(configJson);
        String imagesJsonString = configJSONObject.getString(CONFIG_IMAGES_KEY);
        JSONObject imagesJSONObject = new JSONObject(imagesJsonString);
        String baseUrl = imagesJSONObject.getString(CONFIG_BASE_URL_KEY);
        JSONArray posterJSONArray = imagesJSONObject.getJSONArray(CONFIG_POSTER_KEY);
        String posterSize = "";
        for (int i = 0; i < posterJSONArray.length(); i++) {
            if (posterJSONArray.getString(i).equals(CONFIG_POSTER_SIZE))
                posterSize = posterJSONArray.getString(i);
        }
        //  https://image.tmdb.org/t/p/ + w185
        return baseUrl + posterSize;
    }

    public static String[] getMovieValues(String movieJsonStr, String movieKey)
            throws JSONException {

        final String MOVIEDB_RESULTS = "results";

        JSONObject moviesJSONObject = new JSONObject(movieJsonStr);
        JSONArray movieArray = moviesJSONObject.getJSONArray(MOVIEDB_RESULTS);
        String[] moviePosterValues = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject oneMovieResult = movieArray.getJSONObject(i);
            String oneMovieValue = oneMovieResult.getString(movieKey); // POSTER_PATH || ID
            moviePosterValues[i] = oneMovieValue;
        }
        // /6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg || 198663
        return moviePosterValues;
    }

    public static MovieDetails getMovieDetails(String detailsJsonStr, String movieFullUrl)
            throws JSONException {

        final String MOVIEDB_TITLE = "original_title";
        final String MOVIEDB_RELEASE_DATE = "release_date";
        final String MOVIEDB_VOTE_AVERAGE = "vote_average";
        final String MOVIEDB_OVERVIEW = "overview";

        JSONObject detailsJSONObject = new JSONObject(detailsJsonStr);
        String movieTitle = detailsJSONObject.getString(MOVIEDB_TITLE);
        String movieReleaseDate = detailsJSONObject.getString(MOVIEDB_RELEASE_DATE);
        String movieVoteAverage = detailsJSONObject.getString(MOVIEDB_VOTE_AVERAGE);
        String movieOverview = detailsJSONObject.getString(MOVIEDB_OVERVIEW);
        return new MovieDetails(
                movieFullUrl, movieTitle, movieReleaseDate, movieVoteAverage, movieOverview);
    }

}
