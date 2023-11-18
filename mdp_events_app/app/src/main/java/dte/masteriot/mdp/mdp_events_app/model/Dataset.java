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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private String date1;
    private String date2;

    public Dataset(String json, String event_type, String date1, String date2) {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        this.event_type = event_type;
        this.date1 = date1;
        this.date2 = date2;
        construct_event_list(json);
    }

    void construct_event_list(String json_str){

        List<String> list = new ArrayList<String>();

        String title, description, price, dtstart, dtend, recurrence, time, link, event_location;
        int is_free, id;
        LatLng latlng;

        boolean match_type, match_date;

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
                match_date = get_match_date(dtstart, dtend);
                if(userDetail.has("recurrence")){
                    JSONObject rec = userDetail.getJSONObject("recurrence");
                    recurrence = rec.getString("days");
                }
                else{
                    recurrence = "NA";
                }
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
                match_type = get_match_type(type);
                if(((event_type == "all") || match_type) && (match_date)){
                    listofitems.add(new Item(title, description, this.event_type, is_free, price,
                            dtstart, dtend, recurrence, time, link, event_location, latlng, (long) id));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean get_match_type(String type){
        Dictionary<String, String> dic = new Hashtable<>();
        dic.put("sport", "/ActividadesDeportivas");
        dic.put("art", "/Exposiciones,/ActividadesCalleArteUrbano");
        dic.put("music", "/Musica");
        dic.put("theater", "/TeatroPerformance,/DanzaBaile,/CineActividadesAudiovisuales" +
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

    public boolean get_match_date(String dtstart, String dtend) {
        boolean match = true;
        String aux_day;

        dtstart = dtstart.substring(0,10);
        dtend = dtend.substring(0,10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date strStartDate = sdf.parse(dtstart);
            Date strEndDate = sdf.parse(dtend);
            Date today = sdf.parse(date1);
            Date last_day = sdf.parse(date2);

            if (strStartDate.after(last_day) || strEndDate.before(today)) {
                match = false;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
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
        int position = listofitems.indexOf(new Item("placeholder", "placeholder", "placeholder", 0, null, null, null, null, null, null, null, null, searchedkey));
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