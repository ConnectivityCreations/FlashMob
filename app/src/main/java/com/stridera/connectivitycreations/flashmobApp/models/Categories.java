package com.stridera.connectivitycreations.flashmobApp.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * A list of all available (currently or previously used) Categories
 */
@Table(name = "Categories")
public class Categories extends Model {
    public static final String TABLE_NAME = "Categories";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long category_id; // Table Key
    public String name; // The name of the category
}