package com.amaslov.android.popularmovies.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import static com.amaslov.android.popularmovies.BuildConfig.YOUTUBE_API_KEY;

public class YtPlayerFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {
    private static final String YT_VIDEO_ID = "yt_id";

    public YtPlayerFragment() {
        // Required empty public constructor
        this.initialize(YOUTUBE_API_KEY, this);
    }

    public static YtPlayerFragment newInstance(String youtubeID) {
        YtPlayerFragment fragment = new YtPlayerFragment();
        Bundle args = new Bundle();
        args.putString(YT_VIDEO_ID, youtubeID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        String youtubeVideoId = "";
        if (getArguments() != null) {
            youtubeVideoId = getArguments().getString(YT_VIDEO_ID);
            youTubePlayer.loadVideo(youtubeVideoId);
            Log.d("onInitializationSuccess", "onInitializationSuccess: " + youtubeVideoId);
        } else {
            Toast.makeText(getContext(), "Youtube" + youtubeVideoId + " Failed!", Toast.LENGTH_SHORT).show();
        }
        youTubePlayer.setShowFullscreenButton(false);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(getContext(), "Youtube Failed!", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
//    public interface OnFragmentInteractionListener {
//        // Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
