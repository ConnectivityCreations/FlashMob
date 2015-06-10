package com.stridera.connectivitycreations.flashmob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Accepted;
import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.List;

public class EventDetailsActivity extends ActionBarActivity {

    public static final String EVENT_ID = "event_id";

    protected ImageView ivEventDetailsImage;
    protected TextView tvEventName;

    protected RelativeLayout rlJoinOrEditViews;
    protected ImageView ivJoinOrEditImage;
    protected TextView tvJoinOrEditLabel;

    protected RelativeLayout rlAttendingViews;
    protected TextView tvAttendingCount;

    protected RelativeLayout rlCommentsViews;
    protected TextView tvCommentsCount;

    protected TextView tvEventTime;
    protected TextView tvEventLocation;

    protected Flashmob event;
    protected String eventId;

    protected ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        currentUser = FlashUser.getCurrentUser();

        getAllViews();

        eventId = getIntent().getStringExtra(EVENT_ID);
        Flashmob.getInBackground(eventId, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob flashmob, ParseException e) {
                event = flashmob;
                setAllViews();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
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

    private void getAllViews()
    {
        ivEventDetailsImage = (ImageView) findViewById(R.id.ivEventDetailsImage);
        tvEventName = (TextView) findViewById(R.id.tvEventName);

        rlJoinOrEditViews = (RelativeLayout) findViewById(R.id.rlJoinOrEditViews);
        ivJoinOrEditImage = (ImageView) findViewById(R.id.ivJoinOrEditImage);
        tvJoinOrEditLabel = (TextView) findViewById(R.id.tvJoinOrEditLabel);

        rlAttendingViews = (RelativeLayout) findViewById(R.id.rlAttendingViews);
        tvAttendingCount = (TextView) findViewById(R.id.tvAttendingCount);

        rlCommentsViews = (RelativeLayout) findViewById(R.id.rlCommentsViews);
        tvCommentsCount = (TextView) findViewById(R.id.tvCommentsCount);

        tvEventTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventLocation = (TextView) findViewById(R.id.tvEventLocation);

        // TODO: Display the min and max number of attendees somewhere
    }

    private void setAllViews() {
        // TODO: Set the image for the event
        // ivEventDetailsImage = ...

        updateAttendingCount();

        // TODO: Set the number of comments
        // tvCommentsCount.setText(flashmob.getTitle());

        tvEventName.setText(event.getTitle());
        tvEventTime.setText(event.getEventDate().toString());
        tvEventLocation.setText(event.getAddress());

        if (currentUser == event.getOwner()) {
            ivJoinOrEditImage.setImageResource(R.drawable.ic_edit_image);
            tvJoinOrEditLabel.setText("Edit");
            // TODO: Set onclick listener for edit view clicked
        } else {
            ivJoinOrEditImage.setImageResource(R.drawable.ic_join_image);
            tvJoinOrEditLabel.setText("Join");

            rlJoinOrEditViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // TODO: Prevent user from joining multiple times
                    event.join();
                    updateAttendingCount();
                    Toast.makeText(EventDetailsActivity.this, "Event joined!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        rlAttendingViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventDetailsActivity.this, AttendeesActivity.class);
                i.putExtra("event_id", event.getObjectId());
                startActivity(i);
            }
        });

        rlCommentsViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventDetailsActivity.this, CommentsActivity.class);
                i.putExtra("event_id", event.getObjectId());
                startActivity(i);
            }
        });
    }

    protected void updateAttendingCount() {
        Accepted.getItemsSelectedByFlashmobInBackground(event, new FindCallback<Accepted>() {
            @Override
            public void done(List<Accepted> list, ParseException e) {
                tvAttendingCount.setText(list.size() + "");
            }
        });
    }

}
