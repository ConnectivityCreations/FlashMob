package com.stridera.connectivitycreations.flashmob.fragments;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.List;

public class StreamMapFragment extends Fragment implements
        GoogleMap.OnCameraChangeListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "FlashmobStreamFragmentMap";

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location current_location;

    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    String flashmob_id;
    ArrayList<String> mapped_ids;

    // newInstance constructor for creating fragment with arguments
    public static StreamMapFragment newInstance(String flashmob_id) {
        StreamMapFragment streamMapFragment = new StreamMapFragment();
//        Bundle args = new Bundle();
//        args.putString("flashmob_id", flashmob_id);
//        streamMapFragment.setArguments(args);
        return streamMapFragment;
    }

    // Called from the activity whenever it wants us to update date... for example on item created
    public void update() {
//        getUpcomingEvents();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapped_ids = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_stream, container, false);

        setupMap();

        return view;
    }

    public void setupMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.streamMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);
            map.setOnCameraChangeListener(this);
//            map.setOnMapLongClickListener(this);
//            map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this).build();

            connectClient();
        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        current_location = location;
                    }
                });
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
//                    marker.showInfoWindow();
                }
            }
        });
    }

    private void getUpcomingEvents(final double latitude, final double longitude, final boolean useLocalDataStore) {
        if (latitude == 0.0f && longitude == 0.0f) return;

        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        Location center=new Location("center");
        center.setLatitude(bounds.getCenter().latitude);
        center.setLongitude(bounds.getCenter().longitude);
        Location corner = new Location("northeast");
        corner.setLatitude(bounds.northeast.latitude);
        corner.setLongitude(bounds.northeast.longitude);

        float dis = center.distanceTo(corner);
        float disMiles = (dis*0.000621371192237334f);

        Log.d("Blah", String.format("Getting new events around %f, %f, with a %f distance", latitude, longitude, disMiles));

        Flashmob.findNearbyEventsInBackground(
                new ParseGeoPoint(latitude, longitude),
                Math.round(disMiles),
                useLocalDataStore,
                new FindCallback<Flashmob>() {
                    @Override
                    public void done(List<Flashmob> list, ParseException e) {
                        Log.d("blah", "Processing " + list.size() + " Items");

                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_GREEN
                                );

                        for (Flashmob flashmob : list) {
                            if (!mapped_ids.contains(flashmob.getObjectId())) {
                                Marker marker = map.addMarker(new MarkerOptions()
                                        .position(flashmob.getLocationLatLong())
                                        .title(flashmob.getTitle())
                                        .snippet(flashmob.getEventDate().toString())
                                        .icon(defaultMarker));
                                dropPinEffect(marker);
                                mapped_ids.add(flashmob.getObjectId());
                                Log.d("blah", "Added marker for " + flashmob.getTitle());
                            } else {
                                Log.d("blah", "Skipped marker for " + flashmob.getTitle());
                            }
                        }

                        if (useLocalDataStore) getUpcomingEvents(latitude, longitude, false);
                    }
                });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        getUpcomingEvents(cameraPosition.target.latitude, cameraPosition.target.longitude, true);
    }
}
