package dte.masteriot.mdp.mdp_events_app.model;

import java.util.List;

public class ItemMQTT {

    private Long EventID;
    private String EventName;

    ItemMQTT(Long EventID, String EventName){
        this.EventID=EventID;
        this.EventName=EventName;
    }

    public Long getEventID() {
        return EventID;
    }

    public String getEventName() {
        return EventName;
    }

}
