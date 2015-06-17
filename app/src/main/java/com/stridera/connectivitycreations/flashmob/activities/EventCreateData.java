package com.stridera.connectivitycreations.flashmob.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Category;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;
import com.stridera.connectivitycreations.flashmob.utils.LocationHelper;
import com.stridera.connectivitycreations.flashmob.utils.TimeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EventCreateData {

  public static final String EVENT_ID = "event_id";
  public static final String CATEGORIES = "categories";
  private static final String TAG = EventCreateData.class.getSimpleName();

  public interface SaveCallback {
    void onSuccess(Flashmob event);
    void onFailure(ParseException ex, String userError);
  }

  private LatLng eventLatLng = null;
  private String eventAddress = null;
  LatLng userLocation = null;
  Calendar startTime = null;
  Calendar endTime = null;
  Bitmap eventImage = null;
  String eventId = null;
  List<Category> categories = new ArrayList<>();

  public EventCreateData() {}

  public static EventCreateData fromFlashmob(Flashmob flashmob, LatLng userLocation) {
    LatLng eventLatLng = flashmob.getLocationLatLong();
    String eventAddress = flashmob.getAddress();
    Calendar startTime = getCalendar(flashmob.getEventDate());
    Calendar endTime = getCalendar(flashmob.getEventEnd());
    String eventId = flashmob.getObjectId();
    Bitmap eventImage = null;
    ArrayList<Category> categories = new ArrayList<>(flashmob.getCategories());
    return new EventCreateData(
        eventLatLng,
        eventAddress,
        userLocation,
        startTime,
        endTime,
        eventImage,
        eventId,
        categories);
  }

  public static EventCreateData fromBundle(Bundle savedInstanceState) {
    LatLng eventLatLng = savedInstanceState.getParcelable("event_location");
    String eventAddress = savedInstanceState.getString("event_address");
    LatLng userLocation = savedInstanceState.getParcelable("user_location");
    Calendar startTime = getTime("start_time", savedInstanceState);
    Calendar endTime = getTime("end_time", savedInstanceState);
    Bitmap eventImage = savedInstanceState.getParcelable("event_image");
    String eventId = savedInstanceState.getString(EVENT_ID);
    String[] categoryIds = savedInstanceState.getStringArray(CATEGORIES);
    ArrayList<Category> categories = new ArrayList<>(categoryIds.length);
    for (String id : categoryIds) {
      categories.add(Category.createWithoutData(Category.class, id));
    }
    return new EventCreateData(
        eventLatLng,
        eventAddress,
        userLocation,
        startTime,
        endTime,
        eventImage,
        eventId,
        categories);
  }

  public EventCreateData(
      LatLng eventLatLng,
      String eventAddress,
      LatLng userLocation,
      Calendar startTime,
      Calendar endTime,
      Bitmap eventImage,
      String eventId,
      List<Category> categories) {
    this.eventLatLng = eventLatLng;
    this.eventAddress = eventAddress;
    this.userLocation = userLocation;
    this.startTime = startTime;
    this.endTime = endTime;
    this.eventImage = eventImage;
    this.eventId = eventId;
    this.categories = categories;
    try {
      for (Category category : categories) {
        category.fetchIfNeeded();
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public void saveState(Bundle outState) {
    outState.putParcelable("event_location", eventLatLng);
    outState.putString("event_address", eventAddress);
    outState.putParcelable("user_location", userLocation);
    if (startTime != null) {
      outState.putLong("start_time", startTime.getTime().getTime());
    }
    if (endTime != null) {
      outState.putLong("end_time", endTime.getTime().getTime());
    }
    outState.putParcelable("event_image", eventImage);
    outState.putString(EVENT_ID, eventId);
    String[] categoryIds = new String[categories.size()];
    int i = 0;
    for (Category category : categories) {
      categoryIds[i] = category.getObjectId();
      i++;
    }
    outState.putStringArray(CATEGORIES, categoryIds);
  }

  public void saveFlashmob(String title, Integer minAttendees, Integer maxAttendees, final SaveCallback callback) {
    if (title.isEmpty()) {
      callback.onFailure(null, "Event name is required");
      return;
    }
    if ((eventLatLng == null) || (eventAddress == null)) {
      callback.onFailure(null, "Event location is required");
      return;
    }

    ParseGeoPoint location = new ParseGeoPoint(eventLatLng.latitude, eventLatLng.longitude);

    // optional
    Date when = startTime == null ? null : startTime.getTime();
    Date end = endTime == null ? null : endTime.getTime();

    final Flashmob event = eventId == null ? new Flashmob() : Flashmob.createWithoutData(Flashmob.class, eventId);
    event.set(title, eventImage, when, end, minAttendees, maxAttendees, location, eventAddress, categories);
    event.saveInBackground(new com.parse.SaveCallback() {
      @Override
      public void done(com.parse.ParseException e) {
        if (e == null) {
          callback.onSuccess(event);
        } else {
          callback.onFailure(e, "Error saving your event");
        }
      }
    });
  }

  public LatLng getEventLatLng() {
    return eventLatLng;
  }

  public String getEventAddress() {
    return eventAddress;
  }

  public void setLocation(LatLng latLng, Address address) {
    this.eventLatLng = latLng;
    this.eventAddress = LocationHelper.addressToString(address);
  }

  static Calendar getTime(String key, Bundle savedInstanceState) {
    long time = savedInstanceState.getLong(key);
    if (time != 0) {
      Calendar cal = new GregorianCalendar();
      cal.setTime(new Date(time));
      return cal;
    }
    return null;
  }

  static Calendar getCalendar(Date time) {
    if (time == null) {
      return null;
    }
    Calendar cal = new GregorianCalendar();
    cal.setTime(time);
    return cal;
  }
}
