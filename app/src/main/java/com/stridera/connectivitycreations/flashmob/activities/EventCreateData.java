package com.stridera.connectivitycreations.flashmob.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;
import com.stridera.connectivitycreations.flashmob.utils.LocationHelper;
import com.stridera.connectivitycreations.flashmob.utils.TimeHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EventCreateData {
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

  public EventCreateData() {}

  public EventCreateData(Flashmob flashmob, LatLng userLocation) {
    eventLatLng = flashmob.getLocationLatLong();
    eventAddress = flashmob.getAddress();
    startTime = getCalendar(flashmob.getEventDate());
    endTime = getCalendar(flashmob.getEventEnd());
    this.userLocation = userLocation;
    eventId = flashmob.getObjectId();
  }

  public EventCreateData(Bundle savedInstanceState) {
    eventLatLng = savedInstanceState.getParcelable("event_location");
    eventAddress = savedInstanceState.getString("event_address");
    userLocation = savedInstanceState.getParcelable("user_location");
    startTime = getTime("start_time", savedInstanceState);
    endTime = getTime("end_time", savedInstanceState);
    eventImage = savedInstanceState.getParcelable("event_image");
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
    event.set(title, eventImage, when, end, minAttendees, maxAttendees, location, eventAddress);
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
