package com.stridera.connectivitycreations.flashmob.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Accepted")
public class Accepted extends ParseObject {

    public static void getItemsSelectedByCurrentUserInBackground(final FindCallback<Accepted> callback) {
        ParseQuery<Accepted> query = new ParseQuery<Accepted>(Accepted.class);
        query.include("flashmob");
        query.whereEqualTo("user", FlashUser.getCurrentuser());
        query.findInBackground(new FindCallback<Accepted>() {
            @Override
            public void done(List<Accepted> flashmobs, ParseException e) {
                if (e == null) {
                    callback.done(flashmobs, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void getItemsSelectedByFlashmobInBackground(Flashmob flashmob, final FindCallback<Accepted> callback) {
        ParseQuery<Accepted> query = new ParseQuery<Accepted>(Accepted.class);
        query.include("flashmob");
        query.whereEqualTo("flashmob", flashmob);
        query.findInBackground(new FindCallback<Accepted>() {
            @Override
            public void done(List<Accepted> flashmobs, ParseException e) {
                if (e == null) {
                    callback.done(flashmobs, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }
}
