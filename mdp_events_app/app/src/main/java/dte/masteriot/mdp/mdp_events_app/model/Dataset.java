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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dataset {

    // This dataset is a list of Items
    private static final String TAG = "TAGListOfItems, Dataset";
    private List<Item> listofitems;
    private String event_type;

    public Dataset(String json, String event_type) {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        this.event_type = event_type;
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


                String type;
                if(userDetail.has("@type")){
                    type = userDetail.getString("@type");
                }
                else{
                    type = "NA";
                }
                boolean match = get_match_type(type);
                if((event_type == "all") || match){
                    listofitems.add(new Item(title, description, this.event_type, is_free, price,
                            dtstart, dtend, time, link, event_location, latlng, (long) id));
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean get_match_type(String type){
        Dictionary<String, String> dic = new Hashtable<>();
        dic.put("deporte", "/ActividadesDeportivas");
        dic.put("expo_arte", "/Exposiciones,/ActividadesCalleArteUrbano");
        dic.put("musica", "/Musica");
        dic.put("teatro_av", "/TeatroPerformance,/DanzaBaile,/CineActividadesAudiovisuales" +
                "/CircoMagia,/CuentacuentosTiteresMarionetas");

        boolean match = false;
        String[] values = dic.get(this.event_type).split(",");

        for(int i = 0; i < values.length; i++){
            Pattern pattern = Pattern.compile(values[i]);
            Matcher matcher = pattern.matcher(type);
            if (matcher.find()) {
                match = true;
            }
        }
        return match;
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
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", "placeholder", 0, null, null, null, null, null, null, null, searchedkey));
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