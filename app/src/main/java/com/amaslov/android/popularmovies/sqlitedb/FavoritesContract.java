package com.amaslov.android.popularmovies.sqlitedb;

import android.provider.BaseColumns;

public class FavoritesContract {

    private FavoritesContract() {
    }

    public static class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME_MOVIE_FAVORITES = "movieFavorites";
        public static final String COLUMN_MOVIE_FULL_URL = "movieFullUrl";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movieVoteAverage";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "movieVoteCount";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
    }
}
