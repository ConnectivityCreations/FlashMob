package com.stridera.connectivitycreations.flashmob.Models;

import android.graphics.Bitmap;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The base class for all flashmobs.
 */
@ParseClassName("Flashmob")
public class Flashmob extends ParseObject {

    public static final int DEFAULT_DURATION = 60;

    // Constructor

    public Flashmob() {
        super();
    }

    public Flashmob(String title, Bitmap image, Date when, Integer duration, Integer min_attendees, Integer max_attendees, ParseGeoPoint location, String address) {
        super();

        if (image != null) {
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
          // get byte array here
          byte[] data = stream.toByteArray();
          UUID uuid = UUID.nameUUIDFromBytes(data);
          ParseFile parseimage = new ParseFile(uuid.toString() + ".jpg", data);
          parseimage.saveInBackground();
          put("image", parseimage);
        }

        put("name", title);
        put("eventAt", (when == null ? new Date() : when));
        put("duration", (duration == null ? DEFAULT_DURATION : duration));
        if (min_attendees != null) {
          put("minAttendees", min_attendees);
        }
        if (max_attendees != null) {
          put("maxAttendees", max_attendees);
        }
        put("location", location);
        put("address", address);
        put("owner", ParseUser.getCurrentUser());
    }

    // Accessor Methods
    public String getTitle() {
        return getString("name");
    }

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public FlashUser getOwner() {
        return (FlashUser) get("User");
    }

    public int getDuration() {
        return getInt("duration");
    }

    public int getMinAttendees() {
        return getInt("minAttendees");
    }

    public int getMaxAttendees() {
        return getInt("maxAttendees");
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public String getAddress() {
        return getString("address");
    }

    public Date getEventDate() {
        return getDate("eventAt");
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder(getTitle()).append("\n")
          .append(getAddress()).append("\n")
          .append(getEventDate());
      return stringBuilder.toString();
    }

    // Static Accessors
    private static ParseQuery<Flashmob> createQuery() {
        ParseQuery<Flashmob> query = new ParseQuery<Flashmob>(Flashmob.class);
        query.include("owner");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public static void getInBackground(final String objectId,
                                       final GetCallback<Flashmob> callback) {
        ParseQuery<Flashmob> query = Flashmob.createQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<Flashmob>() {
            @Override
            public void done(List<Flashmob> objects, ParseException e) {
                if (objects != null) {
                    // Emulate the behavior of getFirstInBackground by using
                    // only the first result.
                    if (objects.size() < 1) {
                        callback.done(null, new ParseException(
                                ParseException.OBJECT_NOT_FOUND,
                                "No flashmob with id " + objectId + " was found."));
                    } else {
                        callback.done(objects.get(0), e);
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void findInBackground(String title,
                                        final GetCallback<Flashmob> callback) {
        ParseQuery<Flashmob> flashmobQuery = ParseQuery.getQuery(Flashmob.class);
        flashmobQuery.whereEqualTo("title", title);
        flashmobQuery.getFirstInBackground(new GetCallback<Flashmob>() {

            @Override
            public void done(Flashmob flashmob, ParseException e) {
                if (e == null) {
                    callback.done(flashmob, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void findNearbyEventsInBackground(ParseGeoPoint location, int distance,
                                                    final FindCallback<Flashmob> callback) {
        ParseQuery<Flashmob> flashmobQuery = ParseQuery.getQuery(Flashmob.class);
        //flashmobQuery.whereNear("location", location);
        flashmobQuery.whereWithinMiles("location", location, distance);
        flashmobQuery.setLimit(distance);
        flashmobQuery.findInBackground(new FindCallback<Flashmob>() {
            @Override
            public void done(List<Flashmob> flashmobs, ParseException e) {
                if (e == null) {
                    callback.done(flashmobs, null);
                } else {
                    callback.done(null, e);
                }
            }
        });

    }

}
