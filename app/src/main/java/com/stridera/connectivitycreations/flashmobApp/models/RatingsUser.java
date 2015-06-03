package com.stridera.connectivitycreations.flashmobApp.models;

import com.activeandroid.annotation.Table;

/**
 * Ratings for Users
 * Extends from @link Ratings
 */
@Table(name = "User")
public class RatingsUser extends Ratings {
    public static final String TABLE_NAME = "RatingsUser";
}
