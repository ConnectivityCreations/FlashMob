package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.fragments.FiltersDialog;
import com.stridera.connectivitycreations.flashmob.fragments.StreamListFragment;
import com.stridera.connectivitycreations.flashmob.fragments.StreamMapFragment;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.Date;

public class StreamActivity extends AppCompatActivity implements StreamListFragment.OnItemSelectedListener, FiltersDialog.FiltersChangedListener {
    private static final String LOG_TAG = "FlashmobStreamActivity";

    // Fragment State
    private static final int VIEW_NOT_LOADED = -1;
    private static final int VIEW_LIST = 0;
    private static final int VIEW_MAP = 1;

    // Menu Items
    private static final int MENU_ITEM_SEPARATOR = 0;
    private static final int MENU_ITEM_HEADER = 2;
    private static final int MENU_ITEM_ALL_ITEMS = 3;
    private static final int MENU_ITEM_MY_ITEMS = 4;

    private int current_view;

    ParseGeoPoint point = new ParseGeoPoint(37.4020619, -122.1144424);
    Fragment fragment;
    MenuItem muMap;
    MenuItem muList;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        muMap = menu.findItem(R.id.action_map_view);
        muList = menu.findItem(R.id.action_list_view);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        current_view = VIEW_NOT_LOADED;

        setupToolbar();
        setupFab();
        initLocation();

        startListView();
//        startMapView();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    private void startListView() {
        if (current_view == VIEW_LIST)
            return;
        current_view = VIEW_LIST;
        if (muList != null)
            muList.setVisible(false);
        if (muMap != null)
            muMap.setVisible(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new StreamListFragment();
        ft.replace(R.id.flFragments, fragment);
        ft.commit();

        setTitle("Local Flashmobs");
    }

    private void startMapView() {
        if (current_view == VIEW_MAP)
            return;
        current_view = VIEW_MAP;
        if (muList != null)
            muList.setVisible(true);
        if (muMap != null)
            muMap.setVisible(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new StreamMapFragment();
        ft.replace(R.id.flFragments, fragment);
        ft.commit();

        setTitle("Local Flashmobs");
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateNewActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
//            updateViewsWithProfileInfo();
            Log.d(LOG_TAG, "User not null");
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            startLoginActivity();
            return true;
        } else if (id == R.id.action_map_view) {
            startMapView();
        } else if (id == R.id.action_list_view) {
            startListView();
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_set_filters) {
            set_filters();
            return true;
        } else if (id == R.id.action_add_test_data) {
            Date now = new Date();
            Date inTwoHours = new Date(now.getTime() + (1000 * 60 * 60 * 2));
            new Flashmob(
                    "Test Event",
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                    inTwoHours,
                    null,
                    2,
                    4,
                    new ParseGeoPoint(37.4020619, -122.1144424),
                    "Box 4440 El Camino Real Los Altos, CA 94022",
                    null
            ).saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    updateFrament(false);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void set_filters() {
        FragmentManager fm = getSupportFragmentManager();
        FiltersDialog editFiltersDialog = new FiltersDialog();
        editFiltersDialog.show(fm, "edit_filters_dialog");
    }

    @Override
    public void onFiltersChanged() {
        updateFrament(true);
    }

    private void updateFrament(boolean prefsUpdated) {
        if (current_view == VIEW_LIST)
            if (prefsUpdated)
                ((StreamListFragment) fragment).updatePrefs();
            else
                ((StreamListFragment) fragment).update();
        else
            ((StreamMapFragment) fragment).update();
    }

    private void initLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//              updateFragment();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        }, null);
    }


    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startCreateNewActivity() {
        Intent intent = new Intent(this, EventCreateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFlashmobSelected(String flashmob_id) {
        Intent i = new Intent(StreamActivity.this, EventDetailsActivity.class);
        Log.d(LOG_TAG, "Starting new event for event_id: " + flashmob_id);
        i.putExtra("event_id", flashmob_id);
        startActivity(i);
    }

    @Override
    public void onFlashmobSelected(String flashmob_id, ImageView imageView) {
        Intent i = new Intent(StreamActivity.this, EventDetailsActivity.class);
        Log.d(LOG_TAG, "Starting new event for event_id: " + flashmob_id);
        i.putExtra("event_id", flashmob_id);
        ActivityOptions ao = ActivityOptions.makeSceneTransitionAnimation(this, imageView, "eventImage");
        startActivity(i, ao.toBundle());
    }

}
