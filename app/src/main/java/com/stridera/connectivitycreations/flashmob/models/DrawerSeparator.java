package com.stridera.connectivitycreations.flashmob.models;

public class DrawerSeparator extends DrawerBaseItem {
    String title;

    @Override
    public int getItemType() {
        return TYPE_SEPARATOR;
    }

    public DrawerSeparator(int id, String title) {
        this.itemId = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
