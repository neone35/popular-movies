package com.amaslov.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by aarta on 2018-02-24.
 */

public class MovieDBJsonUtils {

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

    public static String[] getMoviePosterJPGs(String movieJsonStr)
            throws JSONException {

        final String MOVIEDB_RESULTS = "results";
        final String MOVIEDB_POSTER_PATH = "poster_path";

        String[] moviePosterJPGpaths = null;
        JSONObject moviesJSONObject = new JSONObject(movieJsonStr);
        JSONArray movieArray = moviesJSONObject.getJSONArray(MOVIEDB_RESULTS);
        moviePosterJPGpaths = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject oneMovieResult = movieArray.getJSONObject(i);
            String oneMoviePosterPath = oneMovieResult.getString(MOVIEDB_POSTER_PATH);
            moviePosterJPGpaths[i] = oneMoviePosterPath;
        }
        // /6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg
        return moviePosterJPGpaths;
    }


}
