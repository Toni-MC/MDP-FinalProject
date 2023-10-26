package dte.masteriot.mdp.mdp_events_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dataset {

    // This dataset is a list of Items
    private static final String TAG = "TAGListOfItems, Dataset";
    private List<Item> listofitems;

    Dataset(String json) {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        if (json == null){
            listofitems.add(new Item("Press the button to load events", null, (int) 0, null,
                            null, null, null, null, null, (long) 0));
        }
        else{
            construct_event_list(json);
        }
    }

    void construct_event_list(String json_str){

        List<String> list = new ArrayList<String>();

        String title, description, price, dtstart, dtend, time, link, event_location;
        int is_free, id;

        try {
            JSONObject json_obj;

            json_obj = new JSONObject(json_str);

            JSONArray json_array = json_obj.getJSONArray("@graph");
            for (int i = 0; i < json_array.length(); i++) {
                // create a JSONObject for fetching single user data
                JSONObject userDetail = json_array.getJSONObject(i);
                // fetch email and name and store it in arraylist
                title = userDetail.getString("title");
                description = userDetail.getString("description");
                is_free = userDetail.getInt("free");
                price = userDetail.getString("price");
                dtstart = userDetail.getString("dtstart");
                dtend = userDetail.getString("dtend");
                time = userDetail.getString("time");
                link = userDetail.getString("link");
                event_location = userDetail.getString("event-location");
                id = Integer.valueOf(userDetail.getInt("id"));

                listofitems.add(new Item(title, description, is_free, price,
                                        dtstart, dtend, time, link, event_location, (long) id));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    int getSize() {
        return listofitems.size();
    }

    Item getItemAtPosition(int pos) {
        return listofitems.get(pos);
    }

    Long getKeyAtPosition(int pos) {
        return (listofitems.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        // The following works because in Item, the method "equals" is overriden to compare only keys:
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", 0, null, null, null, null, null, null, searchedkey));

//        int position = listofitems.indexOf(new Item("placeholder", "placeholder", searchedkey));
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        return position;
    }

    void removeItemAtPosition(int i) {
        listofitems.remove(i);
    }

    void removeItemWithKey(Long key) {
        removeItemAtPosition(getPositionOfKey(key));
    }

}