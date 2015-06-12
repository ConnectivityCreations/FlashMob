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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.Date;

public class StreamActivity extends AppCompatActivity implements StreamListFragment.OnItemSelectedListener {
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

        current_view = VIEW_NOT_LOADED;

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

        final ArrayList drawerItems = new ArrayList<>();
        FlashUser user = FlashUser.getCurrentUser();

        drawerItems.add(new DrawerHeader(MENU_ITEM_HEADER, user.getName(), user.getEmail(), user.getBio(), user.getAvatarURL()));
        drawerItems.add(new DrawerSeparator(MENU_ITEM_SEPARATOR, "Separator"));
        drawerItems.add(new DrawerItem(MENU_ITEM_ALL_ITEMS, "All Items", R.drawable.ic_action_add));
        drawerItems.add(new DrawerItem(MENU_ITEM_MY_ITEMS, "My Items", R.drawable.ic_action_add));

        drawerAdapter = new StreamDrawerAdapter(drawerItems, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(drawerAdapter);

        // Handle clicks
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View childViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (childViewUnder != null && gestureDetector.onTouchEvent(motionEvent)) {
                    int itemClicked = recyclerView.getChildPosition(childViewUnder);
                    DrawerBaseItem item = (DrawerBaseItem) drawerItems.get(itemClicked);
                    int itemId = item.getItemId();

                    switch (itemId) {
                        case MENU_ITEM_HEADER:
                            drawerLayout.closeDrawers();
                            Toast.makeText(StreamActivity.this, "Profile Edit Ability Forthcoming!", Toast.LENGTH_SHORT).show();
                            return true;
                        case MENU_ITEM_ALL_ITEMS:
                            drawerLayout.closeDrawers();
                            startListView();
                            ((StreamListFragment) fragment).viewAllItems();
                            setTitle("All Items");
                            return true;
                        case MENU_ITEM_MY_ITEMS:
                            drawerLayout.closeDrawers();
                            startListView();
                            ((StreamListFragment) fragment).viewMyItems();
                            setTitle("My Items");
                            return true;
                        case MENU_ITEM_SEPARATOR:
                        default:
                            return false;
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

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
