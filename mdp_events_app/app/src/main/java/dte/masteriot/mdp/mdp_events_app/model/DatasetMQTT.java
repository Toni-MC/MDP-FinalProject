package dte.masteriot.mdp.mdp_events_app.model;

import java.util.ArrayList;
import java.util.List;

public class DatasetMQTT {

    private List<ItemMQTT> listofitems;

    public DatasetMQTT(){
        this.listofitems=new ArrayList<>();
    }

    public DatasetMQTT(int n){
        this.listofitems=new ArrayList<>();
        listofitems.add(new ItemMQTT(111L,"Event1"));
    }

    public void add(Long EventID, String EventName){
        this.listofitems.add(new ItemMQTT(EventID,EventName));
    }

    public int getSize() {
        return listofitems.size();
    }

    public ItemMQTT getItemAtPosition(int pos) {
        return listofitems.get(pos);
    }

    public Long getIDAtPosition(int pos) {
        return (listofitems.get(pos).getEventID());
    }

    public String getNameAtPosition(int pos) {return (listofitems.get(pos).getEventName()); }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", "placeholder", "placeholder",0, "placeholder", "placeholder", "placeholder", "placeholder", "placeholder", "placeholder", "placeholder", null, searchedkey));
        return position;
    }

    void removeItemAtPosition(int i) {
        listofitems.remove(i);
    }

}
