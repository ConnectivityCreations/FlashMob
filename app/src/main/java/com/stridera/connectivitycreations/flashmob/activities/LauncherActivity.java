package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseException;
import com.parse.ParseUser;
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
