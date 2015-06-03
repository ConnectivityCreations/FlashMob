package com.stridera.connectivitycreations.flashmobApp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Comments")
public class Comments extends Model {
    public static final String TABLE_NAME = "Comments";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long comment_id; // Comment ID
    public long flashmob_id; // => Flashmob.flashmob_id
    public long created; // Creation Timestamp
    public long user_id; // => User.user_id
    public String comment; // Comment
}
