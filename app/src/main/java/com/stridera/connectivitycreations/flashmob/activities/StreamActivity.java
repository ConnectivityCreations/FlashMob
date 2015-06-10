package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.stridera.connectivitycreations.flashmob.fragments.StreamListFragment;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;
import com.stridera.connectivitycreations.flashmob.R;

import java.util.Date;

public class StreamActivity extends AppCompatActivity implements StreamListFragment.OnItemSelectedListener {
    private static final String LOG_TAG = "FlashmobStreamActivity";


    ParseGeoPoint point = new ParseGeoPoint(37.4020619, -122.1144424);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        initLocation();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragments, new StreamListFragment());
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stream, menu);
        return true;
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
        } else if (id == R.id.action_add) {
            startCreateNewActivity();
            return true;
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
            );

//            fillListview();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLocation() {
      LocationManager locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
      locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
              point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//              fillListview();
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

    // Temp display function



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
