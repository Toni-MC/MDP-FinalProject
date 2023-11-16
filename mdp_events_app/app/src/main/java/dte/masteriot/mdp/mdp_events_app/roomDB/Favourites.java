package dte.masteriot.mdp.mdp_events_app.roomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favourites")
public class Favourites {
    @PrimaryKey
    public Long eventID;

    public String eventName;

    public Favourites(Long eventID, String eventName) {
        this.eventID = eventID;
        this.eventName = eventName;
    }

}