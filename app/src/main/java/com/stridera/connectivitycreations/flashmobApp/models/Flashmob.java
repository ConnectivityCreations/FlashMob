package com.stridera.connectivitycreations.flashmobApp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class for the Flashmob Events.
 */
@Table(name = "Flashmob")
public class Flashmob extends Model {
    public static final String TABLE_NAME = "Flashmob";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long flashmob_id; // Table Key
    public long created; // Timestamp of creation
    public long event_time; // Timestamp of event start time
    public short duration = 60; // Duration of event.  Default = 60  Max=720 (12hours)
    public String name; // Name of event.
    public long owner; // Link to the User @link User.user_id
    public long location;
    public String image;
    public int min_attendees;
    public int max_attendees;
}

/*
id - Primary Key, unique, long - Event ID
created - long - Timestamp of the creation event
event_time - long - Timestamp of the event time
duration - short int, default=60 - Duration in minutes of the event.  Maybe 12hour max? (720min max?)
name - String  - Name of the event
Owner - long -> User.id - ID of the user from the User Class
Location - long -> Location.id - Link to the Location Class
image - String (url) - Url to the default image.  Each event will have one default image.
min_attendees - int, optional - Minimum attendees to consider the event successful
max_attendees - int, optional - Maximum Number of events.
 */