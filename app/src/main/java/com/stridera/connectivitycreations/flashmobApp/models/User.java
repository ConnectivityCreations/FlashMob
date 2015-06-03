package com.stridera.connectivitycreations.flashmobApp.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * The user object on the device.
 *
 * Credentials (OAuth, Password Hash, etc) should be stored in shared prefs.
 */
@Table(name = "User")
public class User extends Model {
    public static final String TABLE_NAME = "User";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long user_id; // Table Key
    public String username; // Username (Email?)
    public String name; // Optional, Full User Name
    public String bio; // Optional, Max 255 Chars, User Supplied Bio
    public String avatar; // Url to the users Avatar. (Default Image)
}