package com.amaslov.android.popularmovies.utilities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by aarta on 2018-02-23.
 */

public class MovieDBUrlUtils {

    public static final String MOVIE_DB_PATH_POPULAR = "popular";
    public static final String MOVIE_DB_PATH_TOP_RATED = "top_rated";
    public static final String MOVIE_DB_PATH_UPCOMING = "upcoming";
    private static final String SCHEME_HTTPS = "https";
    private static final String MOVIE_DB_AUTHORITY = "api.themoviedb.org";
    private static final String MOVIE_DB_API_VERSION = "3";
    private static final String MOVIE_DB_PATH_MOVIE = "movie";
    private static final String MOVIE_DB_API_KEY = "1d2fb9fdadfd0338c67212b5a352abe9";
    private static final String PARAM_API_KEY = "api_key";
    private static final String MOVIE_DB_PATH_CONFIGURATION = "configuration";

    public MovieDBUrlUtils() {
    }

    public static String getConfigUrl() {
        String configUrl = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME_HTTPS)
                .authority(MOVIE_DB_AUTHORITY)
                .appendPath(MOVIE_DB_API_VERSION)
                .appendPath(MOVIE_DB_PATH_CONFIGURATION)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY);
        configUrl = builder.build().toString();
        return configUrl;
    }

    public static String getMoviesUrl(String sortBy) {
        String popularMoviesUrl = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME_HTTPS)
                .authority(MOVIE_DB_AUTHORITY)
                .appendPath(MOVIE_DB_API_VERSION)
                .appendPath(MOVIE_DB_PATH_MOVIE)
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY);
        popularMoviesUrl = builder.build().toString();
        return popularMoviesUrl;
    }


}
