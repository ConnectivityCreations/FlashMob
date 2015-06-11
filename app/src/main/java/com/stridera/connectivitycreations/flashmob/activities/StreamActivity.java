package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.StreamDrawerAdapter;
import com.stridera.connectivitycreations.flashmob.fragments.StreamListFragment;
import com.stridera.connectivitycreations.flashmob.fragments.StreamMapFragment;
import com.stridera.connectivitycreations.flashmob.models.DrawerBaseItem;
import com.stridera.connectivitycreations.flashmob.models.DrawerHeader;
import com.stridera.connectivitycreations.flashmob.models.DrawerItem;
import com.stridera.connectivitycreations.flashmob.models.DrawerSeparator;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.Date;

public class StreamActivity extends AppCompatActivity implements StreamListFragment.OnItemSelectedListener {
    private static final String LOG_TAG = "FlashmobStreamActivity";

    private static final int VIEW_LIST = 0;
    private static final int VIEW_MAP = 1;

    private int current_view;

    ParseGeoPoint point = new ParseGeoPoint(37.4020619, -122.1144424);
    Fragment fragment;
    MenuItem muMap;
    MenuItem muList;

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter drawerAdapter;
    ActionBarDrawerToggle mDrawerToggle;

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

        setupToolbarAndDrawer();
        setupFab();
        initLocation();

        startListView();
//        startMapView();
    }

    private void setupToolbarAndDrawer() {
        // Setup the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Setup the RecyclerView (Item List)
        recyclerView = (RecyclerView) findViewById(R.id.rvDrawerItems);
        recyclerView.setHasFixedSize(true);

        ArrayList drawerItems = new ArrayList<DrawerBaseItem>();
        drawerItems.add(new DrawerHeader("Test Name", "blah@BLah.com", R.drawable.unknown_user));
        drawerItems.add(new DrawerSeparator("Separator"));
        drawerItems.add(new DrawerItem("All Items", R.drawable.ic_action_add));

        drawerAdapter = new StreamDrawerAdapter(drawerItems);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(drawerAdapter);

        // Finally setup the drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.dlStreamDrawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void startListView() {
        current_view = VIEW_LIST;
        if (muList != null)
            muList.setVisible(false);
        if (muMap != null)
            muMap.setVisible(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new StreamListFragment();
        ft.replace(R.id.flFragments, fragment);
        ft.commit();
    }

    private void startMapView() {
        current_view = VIEW_LIST;
        if (muList != null)
            muList.setVisible(true);
        if (muMap != null)
            muMap.setVisible(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new StreamMapFragment();
        ft.replace(R.id.flFragments, fragment);
        ft.commit();
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
        } else if (id == R.id.action_add_test_data) {
            Date now = new Date();
            Date inTwoHours = new Date(now.getTime() + (1000 * 60 * 60 * 2));
            new Flashmob(
                    "Test Event",
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                    inTwoHours,
                    2,
                    2,
                    4,
                    new ParseGeoPoint(37.4020619, -122.1144424),
                    "Box 4440 El Camino Real Los Altos, CA 94022"
            ).saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    updateFrament();
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFrament() {
        if (current_view == VIEW_LIST)
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
}
