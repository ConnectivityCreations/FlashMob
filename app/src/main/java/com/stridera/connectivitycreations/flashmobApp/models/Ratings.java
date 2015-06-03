package com.stridera.connectivitycreations.flashmobApp.models;

import com.activeandroid.annotation.Column;

/**
 * Ratings base class.  Needs to be extended.
 */
public abstract class Ratings {
    public static final String TABLE_NAME = "Ratings";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long ratings_id; // Table Key
    public short rating; // Rating Value  1 - 5
    public String comment; // Optional.  Comment attached to rating
}
