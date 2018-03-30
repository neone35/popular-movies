package com.amaslov.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailerInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieTrailerInfo createFromParcel(Parcel in) {
            return new MovieTrailerInfo(in);
        }

        public MovieTrailerInfo[] newArray(int size) {
            return new MovieTrailerInfo[size];
        }
    };

    private String[] trailerKeys;
    private String[] trailerNames;

    public MovieTrailerInfo(String[] trailerKeys, String[] trailerNames) {
        this.trailerKeys = trailerKeys;
        this.trailerNames = trailerNames;
    }

    private MovieTrailerInfo(Parcel in) {
        this.trailerKeys = in.createStringArray();
        this.trailerNames = in.createStringArray();
    }

    public String[] getTrailerKeys() {
        return trailerKeys;
    }

    public String[] getTrailerNames() {
        return trailerNames;
    }

    public int getTrailerInfoLength() {
        int trailerInfoArrayLength = 0;
        if (trailerKeys.length != 0)
            trailerInfoArrayLength = trailerKeys.length;
        return trailerInfoArrayLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.trailerKeys);
        dest.writeStringArray(this.trailerNames);
    }
}
