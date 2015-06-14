package com.stridera.connectivitycreations.flashmob.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

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
        return getDate("createdAt");
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

}
