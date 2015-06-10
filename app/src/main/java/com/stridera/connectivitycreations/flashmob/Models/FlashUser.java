package com.stridera.connectivitycreations.flashmob.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class FlashUser extends ParseObject {

    public static ParseUser getCurrentuser() {
        return ParseUser.getCurrentUser();
    }
}
