package com.stridera.connectivitycreations.flashmob.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.stridera.connectivitycreations.flashmob.Models.Flashmob;
import com.stridera.connectivitycreations.flashmob.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StreamActivity extends AppCompatActivity {
    private static final String LOG_TAG = "FlashmobLoginActivity";

    ArrayAdapter<String> arrayAdapter; // Temp.  Will go to fragment.
    ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        // TEMP, will move to fragment
        items = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(arrayAdapter);
        fillListview();
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

            fillListview();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Temp display function
    public void fillListview() {
        Flashmob.findNearbyEventsInBackground(
                new ParseGeoPoint(37.4020619, -122.1144424),
                10,
                new FindCallback<Flashmob>() {
                    @Override
                    public void done(List<Flashmob> list, ParseException e) {
                        arrayAdapter.clear();
                        for(Flashmob flashmob : list) {
                            arrayAdapter.addAll(flashmob.getTitle() + " @ " + flashmob.getEventDate().toString());
                        }
                    }
        });
     }


    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startCreateNewActivity() {
        Intent intent = new Intent(this, EventCreateActivity.class);
        startActivity(intent);
    }

}
