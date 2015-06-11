package com.stridera.connectivitycreations.flashmob.models;

/**
 * Created by mjones on 6/11/15.
 */
public abstract class DrawerBaseItem {
    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;
    protected static final int TYPE_SEPARATOR = 2;

    abstract public int getItemType();
}

