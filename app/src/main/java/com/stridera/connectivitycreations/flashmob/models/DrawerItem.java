package com.stridera.connectivitycreations.flashmob.models;

public class DrawerItem extends DrawerBaseItem {
    String title;
    int imgResID;

    @Override
    public int getItemType() {
        return TYPE_ITEM;
    }

    public DrawerItem(String title, int imgResID) {
        this.title = title;
        this.imgResID = imgResID;
    }

    public String getTitle() {
        return title;
    }

    public int getImgResID() {
        return imgResID;
    }
}