package com.stridera.connectivitycreations.flashmobApp.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Many to Many Relationship link between the Categories and Flashmobs
 */
@Table(name = "EventCategories")
public class EventCategories extends Model {
    public static final String TABLE_NAME = "EventCategories";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long event_category_id; // Table Key
    public long flashmob_id; // => Flashmob.flashmob_id
    public long category_id; // => Categories.category.id
}