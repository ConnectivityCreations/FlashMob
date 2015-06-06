package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.stridera.connectivitycreations.flashmob.R;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class EventCreateActivity extends AppCompatActivity {

  private static final int PICK_PHOTO_CODE = 1;
  private static final String TAG = EventCreateActivity.class.getSimpleName();

  private GoogleMap googleMap;
  private EditText locationEditText;
  private LatLng eventLatLng = null;
  private Address eventAddress;
  private Marker locationMarker;
  private LatLng userLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_create_activity);

    // find all the views we use later
    locationEditText = (EditText) findViewById(R.id.locationEditText);

    // init all the things
    initLocation();
    initMap();
    initLocationEditText();
  }

  private void initLocationEditText() {
    locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        updateEventLocationTask.execute(locationEditText.getText().toString());
        return false;
      }
    });
  }

  private void initMap() {
    SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
    mapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        EventCreateActivity.this.googleMap = googleMap;
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);
        googleMap.animateCamera(cameraUpdate);
        updateEventLocation(eventLatLng);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.setMyLocationEnabled(true);
      }
    });
  }

  private void initLocation() {
    LocationManager locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
    locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        userLocation = latLng;
        updateEventLocation(latLng);
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

  private boolean updateEventLocation(LatLng latLng) {
    if (latLng == null) {
      return false;
    }
    try {
      Geocoder geocoder = new Geocoder(this, Locale.getDefault());
      List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
      if (addresses.isEmpty()) {
        return false;
      }
      updateEventLocation(addresses.get(0), latLng);
      return true;
    } catch (IOException e) {
      Log.e(TAG, "Geocoding failed", e);
    }

    return false;
  }

  AsyncTask<String, Void, Address> updateEventLocationTask = new AsyncTask<String, Void, Address>() {
    static final double SEARCH_BOUNDS_PRECISION = .1;
    static final int MAX_RESULTS = 5;

    @Override
    protected Address doInBackground(String... params) {
      String locationName = params[0];
      LatLng target = (userLocation == null) ? eventLatLng : userLocation;

      try {
        Geocoder geocoder = new Geocoder(EventCreateActivity.this, Locale.getDefault());
        List<Address> addresses = target == null ?
            geocoder.getFromLocationName(
                locationName,
                1
            ) : geocoder.getFromLocationName(
                locationName,
                MAX_RESULTS,
                target.latitude - SEARCH_BOUNDS_PRECISION,
                target.longitude - SEARCH_BOUNDS_PRECISION,
                target.latitude + SEARCH_BOUNDS_PRECISION,
                target.longitude + SEARCH_BOUNDS_PRECISION
            );
        if (addresses.isEmpty()) {
          return null;
        }
        if (target == null) {
          return addresses.get(0);
        }

        Address closestAddress = null;
        float minDistance = Float.MAX_VALUE;
        float[] results = new float[1];
        for (Address address : addresses) {
          Location.distanceBetween(target.latitude, target.longitude, address.getLatitude(), address.getLongitude(), results);
          float distance = results[0];
          if (distance < minDistance) {
            minDistance = distance;
            closestAddress = address;
          }
        }
        return closestAddress;
      } catch (IOException e) {
        Log.e(TAG, "Geocoding failed", e);
      }

      return null;
    }

    @Override
    protected void onPostExecute(Address address) {
      if (address == null) {
        // could not find the location, revert
        updateEventLocation(eventAddress, eventLatLng);
      }
      else {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        updateEventLocation(address, latLng);
      }
    }
  };

  private void updateEventLocation(Address address, LatLng latLng) {
    if ((address == null) || (latLng == null)) {
      return;
    }
    updateLocationEditText(address);
    updateGoogleMap(latLng);
    eventLatLng = latLng;
    eventAddress = address;
  }

  private void updateGoogleMap(LatLng latLng) {
    if (googleMap == null) {
      return;
    }

    // marker
    if (this.locationMarker == null) {
      BitmapDescriptor defaultMarker =
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
      locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(defaultMarker));
    } else {
      locationMarker.setPosition(latLng);
    }

    // map camera
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
    googleMap.animateCamera(cameraUpdate);
  }

  private void updateLocationEditText(Address address) {
    StringBuilder addressStringBuilder = new StringBuilder();
    String divider = ", ";
    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
      addressStringBuilder.append(divider).append(address.getAddressLine(i));
    }
    locationEditText.setText(addressStringBuilder.toString().substring(divider.length()));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_event_create, menu);
    return true;
  }

  public void onAttachPhoto(View btn) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, PICK_PHOTO_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Ignoring activity result with code: " + resultCode);
      return;
    }

    if (requestCode == PICK_PHOTO_CODE) {
      if (data != null) {
        Uri photoUri = data.getData();
        try {
          Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
          // Load the selected image into a preview
          ImageView ivPreview = (ImageView) findViewById(R.id.photoImageView);
          ivPreview.setImageBitmap(selectedImage);
        } catch (IOException ex) {
          Log.e(TAG, "Error loading image", ex);
        }
      }
    } else {
      Log.w(TAG, "Unhandled result code: " + resultCode);
    }
  }
}
