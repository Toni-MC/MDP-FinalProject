package dte.masteriot.mdp.mdp_events_app;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;

public class Item {
    // This class contains the actual data of each item of the dataset

    private String title;
    private String description;
    private int is_free;
    private String price;
    private String dtstart;
    private String dtend;
    private String time;
    private String link;
    private String event_location;
    private LatLng latlng;

    private Long key;


    Item(String title, String description, int is_free,
         String price, String dtstart, String dtend,
         String time, String link, String event_location , LatLng latlng, Long key) {

        this.title = title;
        this.description = description;
        this.price = price;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.time = time;
        this.link = link;
        this.event_location = event_location;
        this.latlng = latlng;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Long getKey() {
        return key;
    }


    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Items):
    public boolean equals(Object other) {
        return this.key == ((Item) other).getKey();
    }

}