package com.stridera.connectivitycreations.flashmob.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("_User")
public class FlashUser extends ParseUser {

    public static ParseUser getCurrentuser() {
        return ParseUser.getCurrentUser();
    }
}
