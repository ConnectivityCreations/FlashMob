package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class EventCreateActivity extends AppCompatActivity {

  private static final int PICK_PHOTO_CODE = 1;
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
  private static final String TAG = EventCreateActivity.class.getSimpleName();
  private static final int MILLIS_IN_A_SECOND = 1000;
  private static final int SECONDS_IN_A_MINUTE = 60;
  private static final int MILLIS_IN_A_MINUTE = MILLIS_IN_A_SECOND * SECONDS_IN_A_MINUTE;
  private static final String APP_TAG = "FlashMob";

  private GoogleMap googleMap;
  private EditText locationEditText;
  private TextView startTimeTextView;
  private TextView endTimeTextView;
  private EditText nameEditText;
  private EditText minAttendeesEditText;
  private EditText maxAttendeesEditText;
  private ImageView photoImageView;
  private LatLng eventLatLng = null;
  private Address eventAddress;
  private Marker locationMarker;
  private LatLng userLocation;
  private Calendar startTime;
  private Calendar endTime;
  private Bitmap eventImage;
  private MenuItem progressItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_create_activity);

    // find all the views we use later
    locationEditText = (EditText) findViewById(R.id.locationEditText);
    startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
    endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
    nameEditText = (EditText) findViewById(R.id.nameEditText);
    minAttendeesEditText = (EditText) findViewById(R.id.minAttendeesEditText);
    maxAttendeesEditText = (EditText) findViewById(R.id.maxAttendeesEditText);
    photoImageView = (ImageView) findViewById(R.id.photoImageView);

    // init all the things
    if (savedInstanceState == null) {
      initLocation();
    }
    initMap();
    initLocationEditText();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable("event_location", eventLatLng);
    outState.putParcelable("event_address", eventAddress);
    outState.putParcelable("user_location", userLocation);
    if (startTime != null) {
      outState.putLong("start_time", startTime.getTime().getTime());
    }
    if (endTime != null) {
      outState.putLong("end_time", endTime.getTime().getTime());
    }
    outState.putParcelable("event_image", eventImage);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    eventLatLng = savedInstanceState.getParcelable("event_location");
    eventAddress = savedInstanceState.getParcelable("event_address");
    userLocation = savedInstanceState.getParcelable("user_location");
    startTime = getTime("start_time", savedInstanceState, startTimeTextView);
    endTime = getTime("end_time", savedInstanceState, endTimeTextView);
    eventImage = savedInstanceState.getParcelable("event_image");
    setupPhotoImageView();
  }

  Calendar getTime(String key, Bundle savedInstanceState, TextView timeTextView) {
    long time = savedInstanceState.getLong(key);
    if (time != 0) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(new Date(time));
      setTime(timeTextView, cal);
      return cal;
    }
    return null;
  }

  private void initLocationEditText() {
    locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        new UpdateEventLocationTask().execute(locationEditText.getText().toString());
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

  class UpdateEventLocationTask extends AsyncTask<String, Void, Address> {
    static final double SEARCH_BOUNDS_PRECISION = .2;
    static final int MAX_RESULTS = 5;

    @Override
    protected void onPreExecute() {
      progressItem.setVisible(true);
    }

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
        Toast.makeText(EventCreateActivity.this, "Location could not be found", Toast.LENGTH_LONG).show();
        eventLatLng = null;
        eventAddress = null;
        if (locationMarker != null) {
          locationMarker.remove();
        }
        locationMarker = null;
      }
      else {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        updateEventLocation(address, latLng);
      }
      progressItem.setVisible(false);
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
    String addressStr = addressToString(address);
    locationEditText.setText(addressStr);
  }

  private String addressToString(Address address) {
    StringBuilder addressStringBuilder = new StringBuilder();
    String divider = ", ";
    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
      addressStringBuilder.append(divider).append(address.getAddressLine(i));
    }
    return addressStringBuilder.toString().substring(divider.length());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_event_create, menu);
    progressItem = menu.findItem(R.id.actionProgress);
    ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(progressItem);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_save) {
      saveEvent();
    }

    return super.onOptionsItemSelected(item);
  }

  private void saveEvent() {
    // required
    String title = nameEditText.getText().toString();
    if (title.isEmpty()) {
      Toast.makeText(this, "Event name is required", Toast.LENGTH_LONG).show();
      return;
    }
    if ((eventLatLng == null) || (eventAddress == null)) {
      Toast.makeText(this, "Event location is required", Toast.LENGTH_LONG).show();
      return;
    }
    progressItem.setVisible(true);

    ParseGeoPoint location = new ParseGeoPoint(eventLatLng.latitude, eventLatLng.longitude);
    String address = addressToString(eventAddress);

    // optional
    Date when = startTime == null ? null : startTime.getTime();
    Integer duration = null;
    if (endTime != null) {
      long differenceInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
      duration = (int)(differenceInMillis / MILLIS_IN_A_MINUTE);
    }
    String minStr = minAttendeesEditText.getText().toString();
    Integer minAttendees = minStr.isEmpty() ? null : Integer.valueOf(minStr);
    String maxStr = maxAttendeesEditText.getText().toString();
    Integer maxAttendees = maxStr.isEmpty() ? null : Integer.valueOf(maxStr);
    final Flashmob event = new Flashmob(title, eventImage, when, duration, minAttendees, maxAttendees, location, address);
    event.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e == null) {
          finish();
          Intent i = new Intent(EventCreateActivity.this, EventDetailsActivity.class);
          i.putExtra(EventDetailsActivity.EVENT_ID, event.getObjectId());
          startActivity(i);
        } else {
          Log.e(TAG, "Error saving model", e);
          Toast.makeText(EventCreateActivity.this, "Error saving your event", Toast.LENGTH_LONG).show();
        }
        progressItem.setVisible(false);
      }
    });
  }

  public void onAttachPhoto(View btn) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, PICK_PHOTO_CODE);
  }

  public void onTakePhoto(View view) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri());
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
  }

  private Uri getPhotoFileUri() {
    // Get safe storage directory for photos
    File mediaStorageDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
      Log.d(APP_TAG, "failed to create directory");
    }

    // Return the file target for the photo based on filename
    return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + "photo.jpg"));
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
          eventImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
          setupPhotoImageView();
        } catch (IOException ex) {
          Log.e(TAG, "Error loading image", ex);
        }
      }
    } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      Uri takenPhotoUri = getPhotoFileUri();
      final String imagePath = takenPhotoUri.getPath();

      new AsyncTask<Void,Void,String>() {
        @Override
        protected String doInBackground(Void... params) {
          try {
            return getRightAngleImage(imagePath);
          }catch (Throwable e){
            e.printStackTrace();
          }
          return imagePath;
        }

        @Override
        protected void onPostExecute(String imagePath) {
          super.onPostExecute(imagePath);
          photoImageView.setImageBitmap(decodeFile(imagePath));
        }
      }.execute();
    } else {
      Log.w(TAG, "Unhandled result code: " + resultCode);
    }
  }

  private void setupPhotoImageView() {
    // Load the selected image into a preview
    photoImageView.setImageBitmap(eventImage);
  }

  public void onClickStartTimeTextView(View view) {
    final Calendar now = Calendar.getInstance();
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int minute = now.get(Calendar.MINUTE);

    showTimeDialog(hour, minute, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar time = createAfter(now, hourOfDay, minute);
        setStartTime(time);
      }
    });
  }

  private void setStartTime(Calendar time) {
    setTime(startTimeTextView, time);
    startTime = time;
  }

  public void onClickEndTimeTextView(View view) {
    final TextView timeTextView = (TextView)view;
    final Calendar reference = startTime == null ? Calendar.getInstance() : startTime;
    int hour = reference.get(Calendar.HOUR_OF_DAY) + (Flashmob.DEFAULT_DURATION / 60);
    int minute = reference.get(Calendar.MINUTE) + (Flashmob.DEFAULT_DURATION % 60);

    showTimeDialog(hour, minute, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar time = createAfter(reference, hourOfDay, minute);
        if (startTime == null) {
          setStartTime(reference);
        }
        setTime(timeTextView, time);
        endTime = time;
      }
    });
  }

  private void showTimeDialog(int hour, int minute, TimePickerDialog.OnTimeSetListener listener) {
    new TimePickerDialog(this, listener, hour, minute, DateFormat.is24HourFormat(this)).show();
  }

  private Calendar createAfter(Calendar reference, int hourOfDay, int minute) {
    Calendar time = new GregorianCalendar(reference.get(Calendar.YEAR), reference.get(Calendar.MONTH), reference.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    if (time.before(reference)) {
      time.add(Calendar.DAY_OF_MONTH, 1);
    }
    return time;
  }

  private void setTime(TextView timeTextView, Calendar time) {
    String timeStr = DateFormat.getTimeFormat(EventCreateActivity.this).format(time.getTime());
    timeTextView.setText(timeStr);
  }

  // Handle Image Rotation on Camera Intent

  private String getRightAngleImage(String photoPath) {

    try {
      ExifInterface ei = new ExifInterface(photoPath);
      int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      int degree = 0;

      switch (orientation) {
        case ExifInterface.ORIENTATION_NORMAL:
          degree = 0;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
        case ExifInterface.ORIENTATION_UNDEFINED:
          degree = 0;
          break;
        default:
          degree = 90;
      }

      return rotateImage(degree,photoPath);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return photoPath;
  }

  private String rotateImage(int degree, String imagePath){

    if(degree<=0){
      return imagePath;
    }
    try{
      Bitmap b= BitmapFactory.decodeFile(imagePath);

      Matrix matrix = new Matrix();
      if(b.getWidth()>b.getHeight()){
        matrix.setRotate(degree);
        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                matrix, true);
      }

      FileOutputStream fOut = new FileOutputStream(imagePath);
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

      FileOutputStream out = new FileOutputStream(imagePath);
      if (imageType.equalsIgnoreCase("png")) {
        b.compress(Bitmap.CompressFormat.PNG, 100, out);
      }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
        b.compress(Bitmap.CompressFormat.JPEG, 100, out);
      }
      fOut.flush();
      fOut.close();

      b.recycle();
    }catch (Exception e){
      e.printStackTrace();
    }
    return imagePath;
  }

  public Bitmap decodeFile(String path) {
    try {
      // Decode deal_image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(path, o);
      // The new size we want to sxcale to
      final int REQUIRED_SIZE = 64;

      // Find the correct scale value. It should be the power of 2.
      int scale = 1;
      while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
        scale *= 2;
      // Decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize = scale;
      return BitmapFactory.decodeFile(path, o2);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }
}
