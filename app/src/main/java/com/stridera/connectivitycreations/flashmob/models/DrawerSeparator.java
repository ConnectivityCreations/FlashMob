package com.stridera.connectivitycreations.flashmob.models;

public class DrawerSeparator extends DrawerBaseItem {
    String title;

    @Override
    public int getItemType() {
        return TYPE_SEPARATOR;
    }

    public DrawerSeparator(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
