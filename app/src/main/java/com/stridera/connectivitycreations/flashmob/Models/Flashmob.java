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

/**
 * The base class for all flashmobs.
 */
@ParseClassName("Flashmob")
public class Flashmob extends ParseObject {

    // Constructor

    public Flashmob() {
        super();
    }

    public Flashmob(String title, Bitmap image, Date when, int duration, int min_attendees, int max_attendees, ParseGeoPoint location, String address) {
        super();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        // get byte array here
        byte[] data = stream.toByteArray();
        ParseFile parseimage = new ParseFile(getObjectId() + ".jpg", data);
        parseimage.saveInBackground();

        Flashmob flashmob = ParseObject.createWithoutData(Flashmob.class, this.getObjectId());
        flashmob.put("name", title);
        flashmob.put("image", parseimage);
        flashmob.put("eventAt", when);
        flashmob.put("duration", duration);
        flashmob.put("minAttendees", min_attendees);
        flashmob.put("maxAttendees", max_attendees);
        flashmob.put("location", location);
        flashmob.put("address", address);
        flashmob.put("owner", ParseUser.getCurrentUser());
        flashmob.saveInBackground();
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
