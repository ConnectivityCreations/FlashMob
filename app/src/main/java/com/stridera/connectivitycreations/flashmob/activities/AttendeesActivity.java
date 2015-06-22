package com.stridera.connectivitycreations.flashmob.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.AttendeesAdapter;
import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;

public class AttendeesActivity extends ActionBarActivity {

    public static final String EVENT_ID = "event_id";

    private Flashmob flashmob;
    private ArrayList<FlashUser> attendees;
    private AttendeesAdapter aAttendees;
    private ListView lvAttendees;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendees);

        attendees = new ArrayList<>();
        aAttendees = new AttendeesAdapter(this, attendees);
        lvAttendees = (ListView) findViewById(R.id.lvAttendees);
        lvAttendees.setAdapter(aAttendees);

        eventId = getIntent().getStringExtra(EVENT_ID);
        initToolbar();
        populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_attendees, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateList()
    {
        Flashmob.getInBackground(eventId, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob fm, ParseException e) {
                flashmob = fm;
                getAttendees();
            }
        });
    }

    private void getAttendees() {
        attendees.addAll(flashmob.getAttendees());
        aAttendees.notifyDataSetChanged();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Attendees");
        setSupportActionBar(toolbar);
    }

}
