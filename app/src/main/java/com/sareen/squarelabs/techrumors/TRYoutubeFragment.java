package com.sareen.squarelabs.techrumors;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;


/**
 * Created by Ashish on 02-09-2016.
 */
public class TRYoutubeFragment extends YouTubePlayerSupportFragment
{
    public static TRYoutubeFragment newInstance(String videoUrl)
    {
        TRYoutubeFragment fragment = new TRYoutubeFragment();
        Bundle args = new Bundle();
        args.putString("video_url", videoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public TRYoutubeFragment()
    {}


}

