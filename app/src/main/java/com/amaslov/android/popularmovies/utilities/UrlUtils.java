package com.amaslov.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.amaslov.android.popularmovies.BuildConfig;

public class UrlUtils {

    public static final String MOVIE_DB_PATH_POPULAR = "popular";
    public static final String MOVIE_DB_PATH_TOP_RATED = "top_rated";
    public static final String MOVIE_DB_PATH_UPCOMING = "upcoming";
    private static final String MOVIE_DB_PATH_TRAILERS = "videos";
    private static final String MOVIE_DB_PATH_REVIEWS = "reviews";
    private static final String SCHEME_HTTPS = "https";
    private static final String SCHEME_HTTP = "http";
    private static final String MOVIE_DB_AUTHORITY = "api.themoviedb.org";
    private static final String MOVIE_DB_API_VERSION = "3";
    private static final String MOVIE_DB_PATH_MOVIE = "movie";
    private static final String MOVIE_DB_API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    private static final String PARAM_API_KEY = "api_key";
    private static final String MOVIE_DB_PATH_CONFIGURATION = "configuration";

    public UrlUtils() {
    }

    public static String getConfigUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME_HTTPS)
                .authority(MOVIE_DB_AUTHORITY)
                .appendPath(MOVIE_DB_API_VERSION)
                .appendPath(MOVIE_DB_PATH_CONFIGURATION)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY);
        return builder.build().toString();
    }

    private static Uri buildInitialMovieUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME_HTTPS)
                .authority(MOVIE_DB_AUTHORITY)
                .appendPath(MOVIE_DB_API_VERSION)
                .appendPath(MOVIE_DB_PATH_MOVIE);
        return builder.build();
    }

    public static String getMoviesUrl(String sortBy) {
        Uri initialUri = buildInitialMovieUrl();
        return initialUri.buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY)
                .build().toString();
    }

    public static String getDetailsUrl(String movieId) {
        Uri initialUri = buildInitialMovieUrl();
        return initialUri.buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY)
                .build().toString();
    }

    public static String getTrailersUrl(String movieId) {
        Uri initialUri = buildInitialMovieUrl();
        return initialUri.buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_DB_PATH_TRAILERS)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY)
                .build().toString();
    }

    public static String getReviewsUrl(String movieId) {
        Uri initialUri = buildInitialMovieUrl();
        return initialUri.buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_DB_PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, MOVIE_DB_API_KEY)
                .build().toString();
    }

    public static String[] getYoutubeThumbnailUrls(String[] youtubeKeys) {

        final String YOUTUBE_AUTHORITY = "img.youtube.com";
        final String YOUTUBE_VI = "vi";
        final String YOUTUBE_HQ_ENDING = "/hqdefault.jpg";
        final String YOUTUBE_MQ_ENDING = "/mqdefault.jpg";

        String[] youtubeThumbnailUrls = new String[youtubeKeys.length];

        for (int i = 0; i < youtubeKeys.length; i++) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SCHEME_HTTP)
                    .authority(YOUTUBE_AUTHORITY)
                    .appendPath(YOUTUBE_VI)
                    .appendPath(youtubeKeys[i]);
            String oneThumbnailUrl = builder.build().toString() + YOUTUBE_MQ_ENDING;
            youtubeThumbnailUrls[i] = oneThumbnailUrl;
        }
        return youtubeThumbnailUrls;
    }

}
