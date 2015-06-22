package com.stridera.connectivitycreations.flashmob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Comments;
import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.List;

public class EventDetailsActivity extends ActionBarActivity {

    public static final String EVENT_ID = "event_id";

    protected ImageView ivEventDetailsImage;
    protected TextView tvEventName;

    protected RelativeLayout rlAttendingViews;
    protected TextView tvAttendingCount;

    protected RelativeLayout rlCommentsViews;
    protected TextView tvCommentsCount;

    protected TextView tvEventTime;
    protected TextView tvEventLocation;

    protected Flashmob event;
    protected String eventId;

    private FloatingActionButton fabJoinOrEdit;

    protected ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        currentUser = FlashUser.getCurrentUser();

        getAllViews();

        eventId = getIntent().getStringExtra(EVENT_ID);
        Flashmob.getMostLocalInBackground(eventId, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob flashmob, ParseException e) {
                if (e == null) {
                    event = flashmob;
                    setAllViews();
                } else {
                    Log.d("Blah", "Error: " + e.getMessage());
                    finish();
                }
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

        rlAttendingViews = (RelativeLayout) findViewById(R.id.rlAttendingViews);
        tvAttendingCount = (TextView) findViewById(R.id.tvAttendingCount);

        rlCommentsViews = (RelativeLayout) findViewById(R.id.rlCommentsViews);
        tvCommentsCount = (TextView) findViewById(R.id.tvCommentsCount);

        tvEventTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventLocation = (TextView) findViewById(R.id.tvEventLocation);

        fabJoinOrEdit = (FloatingActionButton) findViewById(R.id.fabJoinOrEdit);


        // TODO: Display the min and max number of attendees somewhere
    }

    private void setAllViews() {

        ParseFile eventPic = event.getImage();

        if (eventPic != null) {
            Picasso.with(this)
                    .load(event.getImage().getUrl())
                    .resize(800, 800)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivEventDetailsImage);
        } else {
            ivEventDetailsImage.setImageResource(R.mipmap.ic_launcher);
        }

        ivEventDetailsImage.setAlpha(90);

        updateAttendingCount();

        updateCommentsCount();

        tvEventName.setText(event.getTitle());
        tvEventTime.setText(event.getEventDate().toString());
        tvEventLocation.setText(event.getAddress());

        if (currentUser == event.getOwner()) {
            fabJoinOrEdit.setImageResource(R.drawable.ic_edit_image_light);
            fabJoinOrEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EventDetailsActivity.this, EventCreateActivity.class);
                    i.putExtra(EventCreateActivity.EVENT_ID, event.getObjectId());
                    i.putExtra(EventCreateActivity.SHOW_DETAILS_POST_SAVE, false);
                    startActivity(i);
                }
            });
        } else {
            if (event.isAttending()) {
                displayUnjoinView();
            } else {
                displayJoinView();
            }
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
                startActivityForResult(i, 255);
            }
        });
    }

    protected void displayJoinView() {
        fabJoinOrEdit.setImageResource(R.drawable.ic_join_image_light);
        fabJoinOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.join();
                updateAttendingCount();
                displayUnjoinView();
                Toast.makeText(EventDetailsActivity.this, "Event joined!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void displayUnjoinView() {
        fabJoinOrEdit.setImageResource(R.drawable.ic_unjoin_image_light);
        fabJoinOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.unjoin();
                updateAttendingCount();
                displayJoinView();
                Toast.makeText(EventDetailsActivity.this, "Event left.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateAttendingCount() {
        tvAttendingCount.setText(String.valueOf(event.getAttendees().size()));
    }

    public void updateCommentsCount()
    {
        Comments.findCommentsInBackground(event, new FindCallback<Comments>() {
            @Override
            public void done(List<Comments> retrievedComments, ParseException e) {
                tvCommentsCount.setText(retrievedComments.size() + "");
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateCommentsCount();
    }

}
