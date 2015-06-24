package com.stridera.connectivitycreations.flashmob.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.StreamAdapter;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.List;

public class StreamListFragment extends Fragment implements LocationSource.OnLocationChangedListener {
    private static final String LOG_TAG = "FlashmobStreamFragList";
    private static final int VIEW_ALL_ITEMS = 0;
    private static final int VIEW_MY_ITEMS = 1;
    private static final String SEARCH_RADIUS = "SearchRadius";
    private static final String SEARCH_LOCATION = "SearchLocation";
    private static final int DEFAULT_SEARCH_RADIUS = 20;
    private static final int DEFAULT_MAX_SEARCH_RADIUS = 100;
    private static final String TAG = StreamListFragment.class.getSimpleName();

    private OnItemSelectedListener listener;

    GoogleApiClient googleApiClient;

    TextView tvNoItemsFound;
    ProgressBar pbLoading;
    TabLayout tlTabs;

    private int current_view = VIEW_ALL_ITEMS;
    private Location searchLocation;
    private int searchRadius;

    // Called from the activity whenever it wants us to update data... for example on item created
    public void update() {
        getUpcomingEvents(true);
    }

    public void updatePrefs() {
        getPrefs();
        getUpcomingEvents(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        float distance = location.distanceTo(searchLocation);
        if (distance > 1.0f) {
            searchLocation = location;
            getUpcomingEvents(true);
        }
    }

    public interface OnItemSelectedListener {
        public void onFlashmobSelected(String flashmob_id);
    }

    StreamAdapter arrayAdapter;
    ArrayList<Flashmob> items;
    SwipeRefreshLayout swipeRefreshLayout;

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
                items.get(position).fetchIfNeededInBackground(new GetCallback<Flashmob>() {
                  @Override
                  public void done(final Flashmob flashmob, ParseException e) {
                    flashmob.pinInBackground(new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        listener.onFlashmobSelected(flashmob.getObjectId());
                      }
                    });
                  }
                });
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUpcomingEvents(false);
            }
        });

        tvNoItemsFound = (TextView) view.findViewById(R.id.tvNoItems);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbStreamProgress);
        pbLoading.setVisibility(View.VISIBLE);

        tlTabs = (TabLayout) view.findViewById(R.id.streamTabs);

        buildGoogleApiClient();

        getPrefs();

        setupTabs();

        getUpcomingEvents(true);

        return view;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(LocationServices.API)
                .build();
    }

    private void getPrefs() {
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", 0);
        String savedLocation = settings.getString(SEARCH_LOCATION, "");
        int savedRadius = settings.getInt(SEARCH_RADIUS, DEFAULT_SEARCH_RADIUS);

        if (savedLocation.isEmpty()) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation == null) {
                searchLocation = new Location("Saved");
                searchLocation.setLatitude(37.4020619);
                searchLocation.setLongitude(-122.1144424);
            } else {
                searchLocation = lastLocation;
                getUpcomingEvents(true);
            }
        }

        searchRadius = savedRadius;
    }

    private void setupTabs() {
        tlTabs.addTab(tlTabs.newTab().setText("All Flashmobs"));
        tlTabs.addTab(tlTabs.newTab().setText("My Flashmobs"));
        tlTabs.setTabMode(TabLayout.MODE_FIXED);
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

        getUpcomingEvents(true);
    }

    private void viewMyItems() {
        if (current_view == VIEW_MY_ITEMS)
            return;

        current_view = VIEW_MY_ITEMS;
        getUpcomingEvents(true);
    }


    private void getUpcomingEvents(boolean useLocalDataStore) {
        if (current_view == VIEW_MY_ITEMS) {
            Log.d(LOG_TAG, "Loading My Items");

            Flashmob.findMyItemsInBackground(useLocalDataStore, new UpdateEventsCallback(useLocalDataStore));
        } else {
            Log.d(LOG_TAG, "Loading Nearby Items");
            Flashmob.findNearbyEventsInBackground(
                    new ParseGeoPoint(searchLocation.getLatitude(), searchLocation.getLongitude()),
                    searchRadius,
                    useLocalDataStore,
                    new UpdateEventsCallback(useLocalDataStore));
        }
    }

    private void updateNoItemsFound(Boolean itemsFound) {
        if (itemsFound)
            tvNoItemsFound.setVisibility(View.GONE);
        else
            tvNoItemsFound.setVisibility(View.VISIBLE);
    }

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, new LocationRequest(), locationListener);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location.distanceTo(searchLocation) > 1) {
                Toast.makeText(getActivity(), "Location Updated", Toast.LENGTH_SHORT).show();
                searchLocation = location;
                getUpcomingEvents(true);
                saveLocation();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    private void saveLocation() {
//        SharedPreferences.Editor editor =
    }

  class UpdateEventsCallback implements FindCallback<Flashmob> {
    private final boolean localDatastoreUsed;

    UpdateEventsCallback(boolean localDatastoreUsed) {
      this.localDatastoreUsed = localDatastoreUsed;
    }

    @Override
    public void done(List<Flashmob> list, ParseException e) {
      arrayAdapter.set(list);
      updateNoItemsFound(!list.isEmpty() || localDatastoreUsed);
      if (localDatastoreUsed) getUpcomingEvents(false);
      else {
        pbLoading.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
      }
    }
  }
}
