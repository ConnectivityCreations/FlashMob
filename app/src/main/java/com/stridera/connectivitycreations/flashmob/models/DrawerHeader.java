package com.stridera.connectivitycreations.flashmob.models;

public class DrawerHeader extends DrawerBaseItem {
    String name;
    String email;
    int profileImage;

    @Override
    public int getItemType() {
        return TYPE_HEADER;
    }

    public DrawerHeader(String name, String email, int profileImage) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getProfileImage() {
        return profileImage;
    }
}
