package dte.masteriot.mdp.mdp_events_app.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Dataset {

    // This dataset is a list of Items
    private static final String TAG = "TAGListOfItems, Dataset";
    private List<Item> listofitems;

    public Dataset(String json) {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        construct_event_list(json);
    }

    void construct_event_list(String json_str){

        List<String> list = new ArrayList<String>();

        String title, description, price, dtstart, dtend, time, link, event_location;
        int is_free, id;
        LatLng latlng;

        try {
            JSONObject json_obj;

            json_obj = new JSONObject(json_str);

            JSONArray json_array = json_obj.getJSONArray("@graph");
            int length = json_array.length();
            for (int i = 0; i < length; i++) {
                // create a JSONObject for fetching single user data
                JSONObject userDetail = json_array.getJSONObject(i);
                title = userDetail.getString("title");
                description = userDetail.getString("description");
                is_free = userDetail.getInt("free");
                price = userDetail.getString("price");
                dtstart = userDetail.getString("dtstart");
                dtend = userDetail.getString("dtend");
                time = userDetail.getString("time");
                link = userDetail.getString("link");
                event_location = userDetail.getString("event-location");
                if(userDetail.has("location")){
                    JSONObject location = userDetail.getJSONObject("location");
                    latlng = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));
                }
                else{
                    latlng = new LatLng(0, 0);
                }
                id = Integer.valueOf(userDetail.getInt("id"));


// img with src ending .png

                listofitems.add(new Item(title, description, is_free, price,
                                        dtstart, dtend, time, link, event_location, latlng, (long) id));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getSize() {
        return listofitems.size();
    }

    public Item getItemAtPosition(int pos) {
        return listofitems.get(pos);
    }

    public Long getKeyAtPosition(int pos) {
        return (listofitems.get(pos).getKey());
    }

    public int getPositionOfKey(Long searchedkey) {
        // Look for the position of the Item with key = searchedkey.
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", 0, null, null, null, null, null, null, null, searchedkey));
        return position;
    }

    void removeItemAtPosition(int i) {
        listofitems.remove(i);
    }

    void removeItemWithKey(Long key) {
        removeItemAtPosition(getPositionOfKey(key));
    }


}



//text.setText(string_result);
//                text.setText(links.toString());