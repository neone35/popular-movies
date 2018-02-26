package com.amaslov.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetails implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    private String moviePosterUrl;
    private String title;
    private String releaseDate;
    private String voteAverage;
    private String voteCount;
    private String overview;

    public MovieDetails(String moviePosterUrl, String title, String releaseDate,
                        String voteAverage, String voteCount, String overview) {
        this.moviePosterUrl = moviePosterUrl;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.overview = overview;
    }

    public MovieDetails(Parcel in) {
        this.moviePosterUrl = in.readString();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readString();
        this.voteCount = in.readString();
        this.overview = in.readString();
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getMovieReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public String getOverview() {
        return overview;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.moviePosterUrl);
        dest.writeString(this.title);
        dest.writeString(this.releaseDate);
        dest.writeString(this.voteAverage);
        dest.writeString(this.voteCount);
        dest.writeString(this.overview);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
