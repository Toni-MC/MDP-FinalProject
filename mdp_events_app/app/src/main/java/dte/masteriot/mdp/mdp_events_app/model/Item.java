package dte.masteriot.mdp.mdp_events_app.model;

import com.google.android.gms.maps.model.LatLng;

public class Item {
    // This class contains the actual data of each item of the dataset

    private String title;
    private String description;

    private String type;
    private String entireType;
    private int is_free;
    private String price;
    private String dtstart;
    private String dtend;
    private String recurrence;
    private String time;
    private String link;
    private String image_link;
    private String event_location;
    private LatLng latlng;

    private Long key;


    Item(String title, String description, String type, String entireType, int is_free,
         String price, String dtstart, String dtend, String recurrence,
         String time, String link, String event_location , LatLng latlng, Long key) {

        this.title = title;
        this.description = description;
        this.type = type;
        this.entireType = entireType;
        this.is_free = is_free;
        this.price = price;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.recurrence = recurrence;
        this.time = time;
        this.link = link;
        this.image_link = "NA";
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

    public int getIs_free() {
        return is_free;
    }

    public String getPrice() {
        return price;
    }

    public String getDtstart() {
        return dtstart;
    }

    public String getDtend() {
        return dtend;
    }

    public String getTime() {
        return time;
    }

    public String getLink() {
        return link;
    }

    public String getEvent_location() {
        return event_location;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public Long getKey() {
        return key;
    }

    public void setImageLink(String link){
        this.image_link = link;
    }

    public String getImageLink(){
        return image_link;
    }
    public String getType(){
        return type;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getEntireType() {
        return entireType;
    }

    public String getImage_link() {
        return image_link;
    }

    // We override the "equals" operator to only compare keys
    // (useful when searching for the position of a specific key in a list of Items):
    public boolean equals(Object other) {
        return this.key == ((Item) other).getKey();
    }

}