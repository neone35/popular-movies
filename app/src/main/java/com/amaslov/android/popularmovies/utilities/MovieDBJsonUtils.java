package com.amaslov.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class MovieDBJsonUtils {

    public static final String MOVIEDB_POSTER_PATH = "poster_path";
    public static final String MOVIEDB_ID = "id";
    private static final String MOVIEDB_RESULTS = "results";

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
        final String MOVIEDB_VOTE_COUNT = "vote_count";
        final String MOVIEDB_OVERVIEW = "overview";

        JSONObject detailsJSONObject = new JSONObject(detailsJsonStr);
        String movieTitle = detailsJSONObject.getString(MOVIEDB_TITLE);
        String movieReleaseDate = detailsJSONObject.getString(MOVIEDB_RELEASE_DATE);
        String movieVoteAverage = detailsJSONObject.getString(MOVIEDB_VOTE_AVERAGE);
        String movieVoteCount = detailsJSONObject.getString(MOVIEDB_VOTE_COUNT);
        String movieOverview = detailsJSONObject.getString(MOVIEDB_OVERVIEW);
        return new MovieDetails(
                movieFullUrl, movieTitle, movieReleaseDate,
                movieVoteAverage, movieVoteCount, movieOverview);
    }

    public static String[] getMovieTrailerKeys(String movieTrailersJsonStr)
            throws JSONException {

        final String MOVIEDB_YT_TRAILER_KEY = "key";

        JSONObject moviesJSONObject = new JSONObject(movieTrailersJsonStr);
        JSONArray trailersArray = moviesJSONObject.getJSONArray(MOVIEDB_RESULTS);
        String[] movieTrailerKeys = new String[trailersArray.length()];

        for (int i = 0; i < trailersArray.length(); i++) {
            JSONObject oneTrailerResult = trailersArray.getJSONObject(i);
            String oneTrailerKeyValue = oneTrailerResult.getString(MOVIEDB_YT_TRAILER_KEY); // POSTER_PATH || ID
            movieTrailerKeys[i] = oneTrailerKeyValue;
        }
        // /6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg || 198663
        return movieTrailerKeys;
    }

}
