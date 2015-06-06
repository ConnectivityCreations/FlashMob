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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

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
import java.util.Locale;


public class EventCreateActivity extends AppCompatActivity {

  private static final int PICK_PHOTO_CODE = 1;
  private static final String TAG = EventCreateActivity.class.getSimpleName();

  private GoogleMap googleMap;
  private EditText locationEditText;
  private Location eventLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_create_activity);

    // find all the views we use later
    locationEditText = (EditText) findViewById(R.id.locationEditText);

    // init everything
    initLocation();
    initMap();
  }

  private void initMap() {
    SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
    mapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        EventCreateActivity.this.googleMap = googleMap;
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);
        googleMap.animateCamera(cameraUpdate);
        updateLocation(eventLocation);
      }
    });

    View mapOverlay = findViewById(R.id.mapOverlay);
    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
    mapOverlay.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        scrollView.requestDisallowInterceptTouchEvent(true);
        return false;
      }
    });
  }

  private void initLocation() {
    LocationManager locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
    locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        updateLocation(location);
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

  private boolean updateLocation(Location location) {
    if (location == null) {
      return false;
    }

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    try {
      Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);

      updateLocationEditText(address);
      updateGoogleMap(location);

      eventLocation = location;
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  private void updateGoogleMap(Location location) {
    if (googleMap == null) {
      return;
    }

    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    // marker
    BitmapDescriptor defaultMarker =
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    Marker mapMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(defaultMarker));

    // map camera
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
    googleMap.animateCamera(cameraUpdate);
  }

  private void updateLocationEditText(Address address) {
    StringBuilder addressStringBuilder = new StringBuilder();
    String divider = ", ";
    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
      String line = address.getAddressLine(i);
      addressStringBuilder.append(divider).append(line);
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
