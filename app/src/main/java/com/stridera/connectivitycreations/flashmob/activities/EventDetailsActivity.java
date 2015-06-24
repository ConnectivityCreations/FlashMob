package com.stridera.connectivitycreations.flashmob.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

    private FloatingActionButton fabJoinOrEdit;

    protected RelativeLayout rlAttendingViews;
    protected TextView tvAttendingCount;

    protected RelativeLayout rlCommentsViews;
    protected TextView tvCommentsCount;

    protected TextView tvEventTime;
    protected TextView tvEventLocation;

    protected RelativeLayout rlMinMaxAttendees;
    protected TextView tvMinMaxAttendees;
    protected View vLastDivider;

    protected Flashmob event;
    protected String eventId;

    private EventCreateData data;

    private GoogleMap googleMap;
    private Marker locationMarker;

    protected ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        initToolbar();
        currentUser = FlashUser.getCurrentUser();

        getAllViews();

        eventId = getIntent().getStringExtra(EVENT_ID);
        Flashmob.getMostLocalInBackground(eventId, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob flashmob, ParseException e) {
                if (e == null) {
                    event = flashmob;
                    data = EventCreateData.fromFlashmob(event, null);
                    initMap();
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

        fabJoinOrEdit = (FloatingActionButton) findViewById(R.id.fabJoinOrEdit);

        rlAttendingViews = (RelativeLayout) findViewById(R.id.rlAttendingViews);
        tvAttendingCount = (TextView) findViewById(R.id.tvAttendingCount);

        rlCommentsViews = (RelativeLayout) findViewById(R.id.rlCommentsViews);
        tvCommentsCount = (TextView) findViewById(R.id.tvCommentsCount);

        tvEventTime = (TextView) findViewById(R.id.tvEventTime);
        tvEventLocation = (TextView) findViewById(R.id.tvEventLocation);

        rlMinMaxAttendees = (RelativeLayout) findViewById(R.id.rlMinMaxAttendees);
        tvMinMaxAttendees = (TextView) findViewById(R.id.tvMinMaxAttendees);
        vLastDivider = findViewById(R.id.lastDivider);
    }

    private void setAllViews() {
        ParseFile eventPic = event.getImage();

        if (eventPic != null) {
            Picasso.with(this)
                    .load(event.getImage().getUrl())
                    .resize(800, 800)
                    .placeholder(R.drawable.default_event_image)
                    .error(R.drawable.default_event_image)
                    .into(ivEventDetailsImage);
        } else {
            ivEventDetailsImage.setImageResource(R.mipmap.ic_launcher);
        }

        updateAttendingCount();
        updateCommentsCount();

        tvEventName.setText(event.getTitle());
        tvEventTime.setText(event.getEventDate().toString());
        tvEventLocation.setText(event.getAddress());

//        Flashmob.getMostLocalInBackground(eventId, new GetCallback<Flashmob>() {
//            @Override
//            public void done(Flashmob flashmob, ParseException e) {
//                if (e == null) {
//                    setData(EventCreateData.fromFlashmob(flashmob, data.userLocation));
//                    ParseFile imageFile = flashmob.getImage();
//                    if (imageFile != null) {
//                        setEventImage(imageFile.getUrl());
//                    }
//                    nameEditText.setText(flashmob.getTitle());
//                    setTextView(minAttendeesEditText, flashmob.getMinAttendees());
//                    setTextView(maxAttendeesEditText, flashmob.getMaxAttendees());
//                } else {
//                    Log.e(TAG, "Error retrieving the event", e);
//                    Toast.makeText(EventCreateActivity.this, "Unable to load your event", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
//        });

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

        Integer minAttendees = event.getMinAttendees();
        Integer maxAttendees = event.getMaxAttendees();

        if (minAttendees == null && maxAttendees == null) {
            rlMinMaxAttendees.setVisibility(View.GONE);
            vLastDivider.setVisibility(View.GONE);
        } else {
            rlMinMaxAttendees.setVisibility(View.VISIBLE);
            vLastDivider.setVisibility(View.VISIBLE);
            if (minAttendees != null && maxAttendees != null) {
                tvMinMaxAttendees.setText("Min: " + minAttendees + "\t\t\t" + "Max: " + maxAttendees);
            } else if (minAttendees != null) {
                tvMinMaxAttendees.setText("Max: " + maxAttendees);
            } else {
                tvMinMaxAttendees.setText("Min: " + minAttendees);
            }
        }

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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Event Details");
        setSupportActionBar(toolbar);
    }

    private void initMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetailsView));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                EventDetailsActivity.this.googleMap = googleMap;
                updateGoogleMap();
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }

    private void updateGoogleMap() {
        if (googleMap == null) {
            return;
        }

        // marker
        LatLng latLng = data.getEventLatLng();
        if (this.locationMarker == null) {
            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(defaultMarker));
        } else {
            locationMarker.setPosition(latLng);
        }

        // map camera
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    private void updateEventLocation() {
        if ((data.getEventAddress() == null) || (data.getEventLatLng() == null)) {
            return;
        }
        updateGoogleMap();
    }

}
