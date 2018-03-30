package com.amaslov.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReviewInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieReviewInfo createFromParcel(Parcel in) {
            return new MovieReviewInfo(in);
        }

        public MovieReviewInfo[] newArray(int size) {
            return new MovieReviewInfo[size];
        }
    };

    private String[] reviewAuthors;
    private String[] reviewContents;

    public MovieReviewInfo(String[] reviewAuthors, String[] reviewContents) {
        this.reviewAuthors = reviewAuthors;
        this.reviewContents = reviewContents;
    }

    private MovieReviewInfo(Parcel in) {
        this.reviewAuthors = in.createStringArray();
        this.reviewContents = in.createStringArray();
    }

    public String[] getReviewAuthors() {
        return reviewAuthors;
    }

    public String[] getReviewContents() {
        return reviewContents;
    }

    public int getReviewInfoLength() {
        int reviewInfoArrayLength = 0;
        if (reviewAuthors.length != 0)
            reviewInfoArrayLength = reviewAuthors.length;
        return reviewInfoArrayLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.reviewAuthors);
        dest.writeStringArray(this.reviewContents);
    }
}

