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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * The base class for all flashmobs.
 */
@ParseClassName("Flashmob")
public class Flashmob extends ParseObject {

    public static final int DEFAULT_DURATION = 60;
    public static final int QUERY_LIMIT = 20;
    public static final String EVENT_NAME = "name";
    public static final String EVENT_OWNER = "owner";
    public static final String EVENT_IMAGE = "image";
    public static final String EVENT_END = "eventEnd";
    public static final String MIN_ATTENDEES = "minAttendees";
    public static final String MAX_ATTENDEES = "maxAttendees";
    public static final String CATEGORIES = "categories";
    private static final String TAG = Flashmob.class.getSimpleName();

    // Constructor

    public Flashmob() {
        super();
    }

    public Flashmob(
            String title,
            Bitmap image,
            Date when,
            Date end,
            Integer min_attendees,
            Integer max_attendees,
            ParseGeoPoint location,
            String address,
            Collection<Category> categories) {
        super();

        set(title, image, when, end, min_attendees, max_attendees, location, address, categories);
    }

    public void set(
            String title,
            Bitmap image,
            Date when,
            Date end,
            Integer min_attendees,
            Integer max_attendees,
            ParseGeoPoint location,
            String address,
            Collection<Category> categories) {
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
        put(EVENT_END, end);
        if (min_attendees != null) {
          put(MIN_ATTENDEES, min_attendees);
        }
        if (max_attendees != null) {
          put(MAX_ATTENDEES, max_attendees);
        }
        put("location", location);
        put("address", address);
        put("owner", ParseUser.getCurrentUser());
        if (categories == null) {
            categories = new ArrayList<>();
        }
        put(CATEGORIES, categories);
    }

    // Accessor Methods
    public String getTitle() {
        return getString(EVENT_NAME);
    }

    public ParseFile getImage() {
        return getParseFile(EVENT_IMAGE);
    }

    public FlashUser getOwner() {
        return (FlashUser) get(EVENT_OWNER);
    }

    public Integer getMinAttendees() {
        return has(MIN_ATTENDEES) ? getInt(MIN_ATTENDEES) : null;
    }

    public Integer getMaxAttendees() {
        return has(MAX_ATTENDEES) ? getInt(MAX_ATTENDEES) : null;
    }

    public List<FlashUser> getAttendees() {
        List<FlashUser> attendees = getList("attendees");
        if (attendees == null) {
            return new ArrayList<>();
        } else {
            return attendees;
        }
    }

    public List<Category> getCategories() {
        List<Category> categories = getList(CATEGORIES);
        if (categories == null) {
            return new ArrayList<>();
        } else {
            return categories;
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

    public Date getEventEnd() {
        return getDate(EVENT_END);
    }

    public Date getEventDate() {
        return getDate("eventAt");
    }

    public String getEventDurationString() {

        long duration = getEventEnd().getTime() - getEventDate().getTime();
        int minutes = Math.round(duration / 60000);
        int hours = 0;

        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }

        String result;
        String hourPlural = "";
        String minutePlural = "";

        if (minutes > 1) minutePlural = "s";
        if (hours > 1) hourPlural = "s";

        if (hours > 0)
            if (minutes == 0)
                result = String.format("%d hour%s", hours, hourPlural);
            else
                result = String.format("%d hour%s, %d minute%s", hours, hourPlural, minutes, minutePlural);
        else
            result = String.format("%d minute%s", minutes, minutePlural);

        return result;
    }

    public void leave() {
        ArrayList<ParseUser> user = new ArrayList<>();
        user.add(ParseUser.getCurrentUser());
        this.removeAll("attendees", user);
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

    public void join() {
        final String owner_id = this.getOwner().getObjectId();
        this.add("attendees", FlashUser.getCurrentUser());
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Blah", "Object saved.");

                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereMatches("user_id", owner_id);

                ParsePush push = new ParsePush();
                push.setMessage(FlashUser.getCurrentUser().getName() + " joined your flashmob!");
                push.setQuery(pushQuery);
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("com.parse.push", "Successfully informed user that we joined");
                        } else {
                            Log.e("com.parse.push", "failed to send join push notification ", e);
                        }
                    }
                });

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
        return getTitle() + "\n" + getAddress() + "\n" + getEventDate();
    }

    // Static Accessors
    private static ParseQuery<Flashmob> createQuery() {
        ParseQuery<Flashmob> query = new ParseQuery<>(Flashmob.class);
        query.include("owner");
        query.include("attendees");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    public static void getMostLocalInBackground(final String objectId, final GetCallback<Flashmob> callback) {
        getInBackground(objectId, true, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob flashmob, ParseException e) {
                if (flashmob != null) {
                    callback.done(flashmob, e);
                    return;
                }

                getInBackground(objectId, false, callback);
            }
        });
    }

    public static void getInBackground(final String objectId,
                                       final GetCallback<Flashmob> callback) {
        getInBackground(objectId, false, callback);
    }

    public static void getInBackground(final String objectId,
                                       final boolean useLocalDataStore,
                                       final GetCallback<Flashmob> callback) {

        ParseQuery<Flashmob> query = Flashmob.createQuery();
        if (useLocalDataStore) query = query.fromLocalDatastore();
        Log.d(TAG, "Fetching from " + (useLocalDataStore ? "local" : "remote") + " datastore");
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
                        objects.get(0).fetchIfNeededInBackground(callback);
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
        flashmobQuery.whereGreaterThan("eventEnd", new Date());
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
