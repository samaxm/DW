package com.sx.dw.test;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sx.dw.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class VideoChatV2ActivityFragment extends Fragment {

    public VideoChatV2ActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_chat_v2, container, false);
    }
}
