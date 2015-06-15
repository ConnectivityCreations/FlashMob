package com.stridera.connectivitycreations.flashmob.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.StreamAdapter;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.List;

public class StreamListFragment extends Fragment {
    private static final String LOG_TAG = "FlashmobStreamFragList";
    private static final int VIEW_ALL_ITEMS = 0;
    private static final int VIEW_MY_ITEMS = 1;

    private OnItemSelectedListener listener;
    TextView tvNoItemsFound;
    ProgressBar pbLoading;
    TabLayout tlTabs;

    private int current_view = VIEW_ALL_ITEMS;

    // Called from the activity whenever it wants us to update date... for example on item created
    public void update() {
        getUpcomingEvents();
    }

    public interface OnItemSelectedListener {
        public void onFlashmobSelected(String flashmob_id);
    }

    ArrayAdapter<Flashmob> arrayAdapter;
    ArrayList<Flashmob> items;
    SwipeRefreshLayout swipeRefreshLayout;

    ParseGeoPoint point = new ParseGeoPoint(37.4020619, -122.1144424);

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUpcomingEvents();
            }
        });

        tvNoItemsFound = (TextView) view.findViewById(R.id.tvNoItems);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbStreamProgress);
        tlTabs = (TabLayout) view.findViewById(R.id.streamTabs);

        setupTabs();

        getUpcomingEvents();

        return view;
    }

    private void setupTabs() {
        tlTabs.addTab(tlTabs.newTab().setText("All Flashmobs"));
        tlTabs.addTab(tlTabs.newTab().setText("My Flashmobs"));
        tlTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    viewAllItems();
                else
                    viewMyItems();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void viewAllItems() {
        if (current_view == VIEW_ALL_ITEMS)
            return;

        current_view = VIEW_ALL_ITEMS;

        getUpcomingEvents();
    }

    private void viewMyItems() {
        if (current_view == VIEW_MY_ITEMS)
            return;

        current_view = VIEW_MY_ITEMS;
        getUpcomingEvents();
    }


    private void getUpcomingEvents() {
        pbLoading.setVisibility(View.VISIBLE);
        if (current_view == VIEW_MY_ITEMS) {
            Log.d(LOG_TAG, "Loading My Items");
            Flashmob.findMyItemsInBackground(
                    new FindCallback<Flashmob>() {
                        @Override
                        public void done(List<Flashmob> list, ParseException e) {
                            arrayAdapter.clear();
                            pbLoading.setVisibility(View.GONE);
                            updateNoItemsFound(!list.isEmpty());
                            arrayAdapter.addAll(list);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
            // Only items owned/accepted
        } else {
            Log.d(LOG_TAG, "Loading Nearby Items");
            Flashmob.findNearbyEventsInBackground(
                    this.point,
                    100,
                    new FindCallback<Flashmob>() {
                        @Override
                        public void done(List<Flashmob> list, ParseException e) {
                            arrayAdapter.clear();
                            pbLoading.setVisibility(View.GONE);
                            updateNoItemsFound(!list.isEmpty());
                            arrayAdapter.addAll(list);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    private void updateNoItemsFound(Boolean itemsFound) {
        if (itemsFound)
            tvNoItemsFound.setVisibility(View.GONE);
        else
            tvNoItemsFound.setVisibility(View.VISIBLE);
    }
}
