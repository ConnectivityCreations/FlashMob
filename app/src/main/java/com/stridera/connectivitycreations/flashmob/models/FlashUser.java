package com.stridera.connectivitycreations.flashmob.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class FlashUser extends ParseUser {

    public String getName() {
        return getString("name");
    }

    public String getBio() {
        return getString("bio");
    }

    public ParseFile getAvatar() {
        return getParseFile("avatar");
    }

    public String getAvatarURL() {
        ParseFile avatar = this.getAvatar();
        if (avatar != null) {
            return avatar.getUrl();
        } else {
            return "";
        }
    }

    public static FlashUser getCurrentUser() {
        return (FlashUser) ParseUser.getCurrentUser();
    }


    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", getName(), getEmail(), getBio(), getAvatarURL());
    }
}
