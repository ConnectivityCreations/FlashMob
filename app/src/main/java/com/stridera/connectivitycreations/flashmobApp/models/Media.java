package com.stridera.connectivitycreations.flashmobApp.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * One to Many Media Objects that can be linked to Flashmobs
 */
@Table(name = "Media")
public class Media extends Model {
    public static final String TABLE_NAME = "Media";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long media_id; // Table Key
    public long flashmob_id; // => Flashmob.flashmob_id
    public String url; // The Url pointing to the image.
}