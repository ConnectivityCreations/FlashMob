package com.stridera.connectivitycreations.flashmob.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * The base class for all flashmobs.
 */
@ParseClassName("Flashmob")
public class Flashmob extends ParseObject {

    public static final int DEFAULT_DURATION = 60;
    public static final int QUERY_LIMIT = 20;

    // Constructor

    public Flashmob() {
        super();
    }

    public Flashmob(String title, Bitmap image, Date when, Date end, Integer min_attendees, Integer max_attendees, ParseGeoPoint location, String address) {
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
        if (when == null) when = new Date();
        put("eventAt", when);

        if (end == null) {
          Calendar calendar = new GregorianCalendar();
          calendar.setTime(when);
          calendar.add(Calendar.MINUTE, DEFAULT_DURATION);
          end = calendar.getTime();
        }
        put("eventEnd", end);
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
        return (FlashUser) get("owner");
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

    public List<FlashUser> getAttendees() {
        List<FlashUser> attendees = getList("attendees");
        if (attendees == null) {
            return new ArrayList<>();
        } else {
            return attendees;
        }
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public LatLng getLocationLatLong() {
        ParseGeoPoint loc = getParseGeoPoint("location");
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    public String getAddress() {
        return getString("address");
    }

    public Date getEventDate() {
        return getDate("eventAt");
    }

    public void join() {
        this.add("attendees", FlashUser.getCurrentUser());
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Blah", "Object saved.");
                if (e != null) {
                    Log.d("Blah", e.toString());
                }
            }
        });
    }

    public void unjoin() {
        this.remove("attendees");
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Blah", "Object saved.");
                if (e != null) {
                    Log.d("Blah", e.toString());
                }
            }
        });
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
        ParseQuery<Flashmob> query = new ParseQuery<>(Flashmob.class);
        query.include("owner");
        query.include("attendees");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
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

    public static void findMyItemsInBackground(final FindCallback<Flashmob> callback) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Flashmob> myItems = ParseQuery.getQuery(Flashmob.class);
        myItems.whereEqualTo("owner", user);

        ParseQuery<Flashmob> joinedItems = ParseQuery.getQuery(Flashmob.class);
        joinedItems.whereEqualTo("attendees", user);

        List<ParseQuery<Flashmob>> queryList = new ArrayList<>();
        queryList.add(myItems);
        queryList.add(joinedItems);

        ParseQuery flashmobQuery = ParseQuery.or(queryList);
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

    public static void findNearbyEventsInBackground(ParseGeoPoint location, int distance,
                                                    final FindCallback<Flashmob> callback) {
        ParseQuery<Flashmob> flashmobQuery = ParseQuery.getQuery(Flashmob.class);
        flashmobQuery.whereGreaterThan("eventAt", new Date());
        //flashmobQuery.whereNear("location", location);
        flashmobQuery.whereWithinMiles("location", location, distance);
        flashmobQuery.setLimit(QUERY_LIMIT);
        flashmobQuery.orderByAscending("eventAt");
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

    public boolean isOwner() {
        return this.getOwner() != null && this.getOwner() == FlashUser.getCurrentUser();
    }

    public boolean isAttending() {
        return this.getAttendees().contains(FlashUser.getCurrentUser());
    }


}
