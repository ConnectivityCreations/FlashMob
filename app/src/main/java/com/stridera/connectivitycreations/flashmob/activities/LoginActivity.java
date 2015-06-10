package com.stridera.connectivitycreations.flashmob.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.stridera.connectivitycreations.flashmob.R;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {
    private static final String LOG_TAG = "FlashmobLoginActivity";

    TextView tvUsername;
    TextView tvPassword;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        Button btnSignup = (Button) findViewById(R.id.btnSignUp);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        ImageButton btnFB = (ImageButton) findViewById(R.id.btnFacebookLogin);

        btnSignup.setOnClickListener(onSignupClickListener);
        btnLogin.setOnClickListener(onLoginClickListener);
        btnFB.setOnClickListener(onFacebookLoginClickListener);
    }

    View.OnClickListener onSignupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(LoginActivity.this, "I'm lazy.  Just use facebook?", Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = tvUsername.getText().toString();
            String password = tvPassword.getText().toString();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        doLoggedIn(false);
                    } else {
                        // Signin failed. Look at the ParseException to see what happened.
                    }
                }
            });
        }
    };

    View.OnClickListener onFacebookLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);
            List<String> permissions = Arrays.asList("public_profile", "email");

            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    progressDialog.dismiss();
                    if (user == null) {
                        Log.d(LOG_TAG, "Uh oh. The user cancelled the Facebook login. ");
                        if (err != null) {
                            Log.d(LOG_TAG, "Exception: " + err.getMessage());
                        }
                    } else {
                        if (user.isNew()) {
                            Log.d(LOG_TAG, "User signed up and logged in through Facebook!");
                        } else {
                            Log.d(LOG_TAG, "User logged in through Facebook!");
                        }

                        doLoggedIn(user.isNew());
                    }
                }
            });
        }
    };

    private void doLoggedIn(boolean isNew) {
        Intent intent = new Intent(LoginActivity.this, StreamActivity.class);
        intent.putExtra("new_user", isNew);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}
