package com.stridera.connectivitycreations.flashmob.models;

import android.text.format.DateUtils;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The base class for all Comments.
 */
@ParseClassName("Comments")
public class Comments extends ParseObject {

    public static final int DEFAULT_DURATION = 60;
    public static final int QUERY_LIMIT = 20;

    // Constructor

    public Comments() {
        super();
    }

    public Comments(String commentText, Flashmob flashmob) {
        super();

        put("Comment", commentText);
        put("flashmob", flashmob);
        put("owner", ParseUser.getCurrentUser());
    }

    // Accessor Methods
    public String getCommentText() {
        return getString("Comment");
    }

    public Flashmob getFlashmob() {
        return (Flashmob) get("flashmob");
    }

    public FlashUser getOwner() {
        return (FlashUser) get("owner");
    }

    public Date getCreatedTime() {
        return getCreatedAt();
    }

    // Static Accessors
    private static ParseQuery<Comments> createQuery() {
        ParseQuery<Comments> query = new ParseQuery<>(Comments.class);
        query.include("Comment");
        query.include("flashmob");
        query.include("owner");
        query.include("createdAt");
        return query;
    }

    public static void findCommentsInBackground(final Flashmob flashmob, final FindCallback<Comments> callback) {
        ParseQuery<Comments> commentsQuery = ParseQuery.getQuery(Comments.class);
        commentsQuery.whereEqualTo("flashmob", flashmob);
        commentsQuery.orderByDescending("createdAt");
        commentsQuery.findInBackground(new FindCallback<Comments>() {
            @Override
            public void done(List<Comments> comments, ParseException e) {
                if (e == null) {
                    callback.done(comments, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    // getRelativeTimeAgo("Sat Jun 13 21:20:24 PDT 2015");
    public String getRelativeTimeAgo() {
        String format = "EEE MMM dd HH:mm:ss 'PDT' yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(getCreatedTime().toString()).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return getShortenedTimeForDisplay(relativeDate);
    }

    private String getShortenedTimeForDisplay(String longFormattedTime)
    {
        if (longFormattedTime.matches("Yesterday")) {
            return "1d";
        } else if (longFormattedTime.matches("[\\d]{1,2} [A-Za-z]+ ago")) {
            String[] longFormattedTimeArray = longFormattedTime.split(" ");
            return longFormattedTimeArray[0] + longFormattedTimeArray[1].charAt(0);
        } else if (longFormattedTime.matches("[A-Za-z]+ [\\d]{1,2}, [\\d]{4}")) {
            String[] longFormattedTimeArray = longFormattedTime.split("[ |,]");
            return longFormattedTimeArray[0] + " " + longFormattedTimeArray[1];
        }

        return longFormattedTime;
    }

}
