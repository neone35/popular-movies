package com.amaslov.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    private String[] fullUrls;
    private String[] ids;

    public MovieInfo(String[] fullUrls, String[] ids) {
        this.fullUrls = fullUrls;
        this.ids = ids;
    }

    MovieInfo(Parcel in) {
        this.fullUrls = in.createStringArray();
        this.ids = in.createStringArray();
    }

    public String[] getFullUrls() {
        return fullUrls;
    }

    public String[] getIds() {
        return ids;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.fullUrls);
        dest.writeStringArray(this.ids);
    }
}
