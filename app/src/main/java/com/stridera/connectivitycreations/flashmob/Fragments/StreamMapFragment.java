package com.stridera.connectivitycreations.flashmob.fragments;

import android.app.Fragment;
import android.os.Bundle;

public class StreamMapFragment extends Fragment {
    String flashmob_id;

    // newInstance constructor for creating fragment with arguments
    public static StreamMapFragment newInstance(String flashmob_id) {
        StreamMapFragment streamMapFragment = new StreamMapFragment();
        Bundle args = new Bundle();
        args.putString("flashmob_id", flashmob_id);
        streamMapFragment.setArguments(args);
        return streamMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flashmob_id = getArguments().getString("flashmob_id");
    }

}
