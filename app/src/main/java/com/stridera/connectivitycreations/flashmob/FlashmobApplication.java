package com.stridera.connectivitycreations.flashmob;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.models.Category;
import com.stridera.connectivitycreations.flashmob.models.Comments;
import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

public class FlashmobApplication extends Application {
    public static final String APP_TAG = "FlashMob";

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(this, Integer.MAX_VALUE)).build();
        Picasso.setSingletonInstance(picasso);

        // Initilize the Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Register ParseObject Subclasses
        ParseObject.registerSubclass(Flashmob.class);
        ParseObject.registerSubclass(FlashUser.class);
        ParseObject.registerSubclass(Comments.class);
        ParseObject.registerSubclass(Category.class);

        // Initialize!
        Parse.initialize(this,
                "g7LvPtBacILfN3dlzKbNExVX6xjA1tk23xd2oVbK",
                "LaBHw0yJYG1NjE96A6ApfePb1TtHMINBnJkT1L8G");

        // Enable Parse Facebook Utils
        ParseFacebookUtils.initialize(this);

        ParseUser user = ParseUser.getCurrentUser();

        // Set this device up to receive Push Notifications
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
