package com.stridera.connectivitycreations.flashmob.models;

public class DrawerHeader extends DrawerBaseItem {
    String name;
    String email;
    String bio;
    String profileImageUrl;

    @Override
    public int getItemType() {
        return TYPE_HEADER;
    }

    public DrawerHeader(int id, String name, String email, String bio, String profileImageUrl) {
        this.itemId = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBio() {
        return bio;
    }
}
