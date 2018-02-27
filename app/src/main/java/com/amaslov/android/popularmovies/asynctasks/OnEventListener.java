package com.amaslov.android.popularmovies.asynctasks;

public interface OnEventListener<T> {
    void onSuccess(T object);
    void onFailure(Exception e);
}
