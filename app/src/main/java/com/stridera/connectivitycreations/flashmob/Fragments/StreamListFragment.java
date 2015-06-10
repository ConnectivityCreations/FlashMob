package com.stridera.connectivitycreations.flashmob.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.stridera.connectivitycreations.flashmob.adapters.StreamAdapter;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;
import com.stridera.connectivitycreations.flashmob.R;

import java.util.ArrayList;
import java.util.List;

public class StreamListFragment extends Fragment {
    private static final String LOG_TAG = "FlashmobStreamFragment";

    private OnItemSelectedListener listener;
    public interface OnItemSelectedListener {
        public void onFlashmobSelected(String flashmob_id);
    }

    ArrayAdapter<Flashmob> arrayAdapter; // Temp.  Will go to fragment.
    ArrayList<Flashmob> items;

    ParseGeoPoint point = new ParseGeoPoint(37.4020619, -122.1144424);

    // newInstance constructor for creating fragment with arguments
    public static StreamListFragment newInstance(String flashmob_id) {
        StreamListFragment streamListFragment = new StreamListFragment();
        return streamListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_stream, container, false);

        items = new ArrayList<Flashmob>();
        arrayAdapter = new StreamAdapter(getActivity(), items);
        ListView lv = (ListView) view.findViewById(R.id.lvItems);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Flashmob flashmob = items.get(position);
                listener.onFlashmobSelected(flashmob.getObjectId());
            }
        });
        fillListview();

        return view;
    }

    public void fillListview() {

        Flashmob.findNearbyEventsInBackground(
                this.point,
                100,
                new FindCallback<Flashmob>() {
                    @Override
                    public void done(List<Flashmob> list, ParseException e) {
                        arrayAdapter.clear();
                        arrayAdapter.addAll(list);
                    }
                });
    }
}
