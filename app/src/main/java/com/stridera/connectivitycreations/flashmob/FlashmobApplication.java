package com.stridera.connectivitycreations.flashmob;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.stridera.connectivitycreations.flashmob.Models.FlashUser;
import com.stridera.connectivitycreations.flashmob.Models.Flashmob;

public class FlashmobApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initilize the Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Register ParseObject Subclasses
        ParseObject.registerSubclass(Flashmob.class);
        ParseObject.registerSubclass(FlashUser.class);

        // Initialize!
        Parse.initialize(this,
                "g7LvPtBacILfN3dlzKbNExVX6xjA1tk23xd2oVbK",
                "LaBHw0yJYG1NjE96A6ApfePb1TtHMINBnJkT1L8G");

        // Enable Parse Facebook Utils
        ParseFacebookUtils.initialize(this);

        // Save current installation for push notifications.
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
