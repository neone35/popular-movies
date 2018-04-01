package com.amaslov.android.popularmovies.utilities;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import com.amaslov.android.popularmovies.parcelables.MovieDetails;
import com.amaslov.android.popularmovies.parcelables.MovieReviewInfo;
import com.amaslov.android.popularmovies.parcelables.MovieTrailerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class JsonUtils {

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
        // /6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg || 198663
        return getParsedArray(moviesJSONObject, movieKey); // movieKey POSTER_PATH | ID
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

    public static MovieTrailerInfo getMovieTrailersInfo(String movieTrailersJsonStr)
            throws JSONException {

        final String MOVIEDB_YT_TRAILER_KEY = "key";
        final String MOVIEDB_YT_TRAILER_NAME = "name";

        JSONObject moviesJSONObject = new JSONObject(movieTrailersJsonStr);
        String[] movieTrailerKeys = getParsedArray(moviesJSONObject, MOVIEDB_YT_TRAILER_KEY);
        String[] movieTrailerNames = getParsedArray(moviesJSONObject, MOVIEDB_YT_TRAILER_NAME);

        // gNXQQbgK_cc || Ti West on INDIANA JONES AND THE LAST CRUSADE
        return new MovieTrailerInfo(movieTrailerKeys, movieTrailerNames);
    }

    public static MovieReviewInfo getMovieReviewsInfo(String movieTrailersJsonStr)
            throws JSONException {

        final String MOVIEDB_REVIEW_AUTHOR = "author";
        final String MOVIEDB_REVIEW_CONTENT = "content";

        JSONObject moviesJSONObject = new JSONObject(movieTrailersJsonStr);
        String[] movieReviewsAuthors = getParsedArray(moviesJSONObject, MOVIEDB_REVIEW_AUTHOR);
        String[] movieReviewsContent = getParsedArray(moviesJSONObject, MOVIEDB_REVIEW_CONTENT);
        // tricksy || Excellent movie. Best of the trilogy...
        return new MovieReviewInfo(movieReviewsAuthors, movieReviewsContent);
    }

    private static String[] getParsedArray(JSONObject initialJSONObject, String JSON_KEY)
            throws JSONException {
        JSONArray inputJSONArray = initialJSONObject.getJSONArray(MOVIEDB_RESULTS);
        String[] parsedValuesArray = new String[inputJSONArray.length()];

        for (int i = 0; i < inputJSONArray.length(); i++) {
            JSONObject oneResult = inputJSONArray.getJSONObject(i);
            String oneValue = oneResult.getString(JSON_KEY);
            parsedValuesArray[i] = oneValue;
        }
        return parsedValuesArray;
    }
}
