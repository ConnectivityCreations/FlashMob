package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stridera.connectivitycreations.flashmob.R;

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        ParseUser currentUser = ParseUser.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            try {
                currentUser.fetch();
                // Save current installation for push notifications.
                ParseUser user = ParseUser.getCurrentUser();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                if (user != null) {
                    installation.put("user_id", user.getObjectId());
                }
                installation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("com.parse.push", "successfully saved device installation data.");
                        } else {
                            Log.e("com.parse.push", "failed to save device installation data", e);
                        }
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
            intent = new Intent(this, StreamActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
