package com.stridera.connectivitycreations.flashmobApp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Accepted")
public class Accepted extends Model {
    public static final String TABLE_NAME = "Accepted";

    // Member Variables
    @Column(index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long accepted_id; // Index ID
    public long flashmob_id; // => @link Flashmob.flashmob_id
    public long user_id; // => @link    User.user_id

    static List<Flashmob> getAcceptedEvents(long user_id) {
        return new Select()
                .from(Flashmob.class)
                .innerJoin(Accepted.class)
                .on(Flashmob.TABLE_NAME + ".flashmob" + " = " + Accepted.TABLE_NAME + ".flashmob_id" )
                .where(Flashmob.TABLE_NAME + ".flashmob = ?", user_id)
                .execute();
    }
}
