package com.amaslov.android.popularmovies.sqlitedb;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final String AUTHORITY = "com.amaslov.android.popularmovies";
    public static final String PATH_FAVORITES = "favorites";
    private static final String SCHEME = "content://";
    static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    private FavoritesContract() {
    }

    public static class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME_MOVIE_FAVORITES = "movieFavorites";
        public static final String COLUMN_MOVIE_FULL_URL = "movieFullUrl";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movieVoteAverage";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "movieVoteCount";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
    }
}
