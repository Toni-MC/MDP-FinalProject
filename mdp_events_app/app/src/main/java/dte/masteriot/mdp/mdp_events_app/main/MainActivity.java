package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import dte.masteriot.mdp.mdp_events_app.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Boolean firstMeasure;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelMainAct";
    String json_str;
    ExecutorService es;
    Handler handler;
    ImageView internetMsg;
    private static final String URL_JSON = "https://datos.madrid.es/egob/catalogo/300107-0-agenda-actividades-eventos.json";
    private static final String  CONTENT_TYPE_JSON = "application/json";

    int n_sport, n_music, n_art, n_theater, n_other, n_courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MeetMadrid"); //No title
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setUpStyle();

        Button bt_sport = findViewById(R.id.bSport);
        Button bt_art = findViewById(R.id.bArt);
        Button bt_music = findViewById(R.id.bMusic);
        Button bt_theater = findViewById(R.id.bTheater);
        Button bt_ws = findViewById(R.id.bCursos);
        Button bt_other = findViewById(R.id.bOther);

        bt_sport.setOnClickListener(this);
        bt_art.setOnClickListener(this);
        bt_music.setOnClickListener(this);
        bt_theater.setOnClickListener(this);
        bt_ws.setOnClickListener(this);
        bt_other.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        boolean internet_conn = InternetIsConnected();
        Intent i;
        if(internet_conn){
            i = new Intent(MainActivity.this , ListActivity.class);
            if(v.getId() == R.id.bSport){
                i.putExtra("event_type","Sport");
            }
            else if(v.getId() == R.id.bMusic){
                i.putExtra("event_type","Music");
            }
            else if(v.getId() == R.id.bArt){
                i.putExtra("event_type","Art");
            }
            else if(v.getId() == R.id.bTheater){
                i.putExtra("event_type","Theater");
            }
            else if(v.getId() == R.id.bCursos){
                i.putExtra("event_type","Workshop");
            }
            else{
                i.putExtra("event_type","Other");
            }
        }
        else{
            i = new Intent(MainActivity.this , InternetConnectionError.class);
        }

        startActivity(i);
    }

    public boolean InternetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void seeStatistics(){
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                String string_result;

                super.handleMessage(msg);
                if((string_result = msg.getData().getString("text")) != null) {
                    json_str = string_result;
                    compute_statistics(json_str);
                    show_statistics();
                }
            }
        };

        if(json_str == null) {
            es = Executors.newSingleThreadExecutor();

            LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_JSON);
            es.execute(loadURLContents);
        }
        else{
            compute_statistics(json_str);
            show_statistics();
        }

    }

    public void compute_statistics(String json_str){

        String type;
        String sport = "/ActividadesDeportivas";
        String[] art = "/Exposiciones,/ActividadesCalleArteUrbano".split(",");
        String music = "/Musica";
        String[] theater = "/TeatroPerformance,/DanzaBaile,/CineActividadesAudiovisuales,/CircoMagia,/CuentacuentosTiteresMarionetas".split(",");
        String[] courses = "/CursosTalleres,/ConferenciasColoquios".split(",");
        n_sport = 0;
        n_music = 0;
        n_art = 0;
        n_theater = 0;
        n_other = 0;
        n_courses = 0;

        try {
            JSONObject json_obj;
            json_obj = new JSONObject(json_str);

            JSONArray json_array = json_obj.getJSONArray("@graph");
            int length = json_array.length();
            for (int i = 0; i < length; i++) {
                // create a JSONObject for fetching single user data
                JSONObject userDetail = json_array.getJSONObject(i);
                if(userDetail.has("@type")){
                    type = userDetail.getString("@type");
                }
                else{
                    type = "NA";
                }
                boolean aux = false;
                if(type.contains(sport)){
                    n_sport++;
                    aux = true;
                }
                else if (type.contains(music)){
                    n_music++;
                    aux = true;
                }
                else{
                    for(int j = 0; j < art.length; j++) {
                        if(type.contains(art[j])){
                            n_art++;
                            aux = true;
                        }
                    }
                    for(int j = 0; j < theater.length; j++) {
                        if(type.contains(theater[j])){
                            n_theater++;
                            aux = true;
                        }
                    }
                    for(int j = 0; j < courses.length; j++) {
                        if(type.contains(courses[j])){
                            n_courses++;
                            aux = true;
                        }
                    }
                }
                if(aux == false){
                    n_other++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void show_statistics(){
        Intent i = new Intent(MainActivity.this , StatsActivity.class);
        i.putExtra("n_sport",n_sport);
        i.putExtra("n_music",n_music);
        i.putExtra("n_art",n_art);
        i.putExtra("n_theater",n_theater);
        i.putExtra("n_other",n_other);
        i.putExtra("n_courses",n_courses);
        startActivity(i);
    }


    public void setUpStyle(){

        SharedPreferences configPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(configPref.contains("static_theme")){
            Boolean selected = configPref.getBoolean("static_theme", false);
            String value = configPref.getString("static_theme_selected", null);

            if(selected){
                sensorManager.unregisterListener(this, lightSensor);
                //static
                if(Objects.equals(value, "light")){
                    changeStyle(0);
                }else if(Objects.equals(value, "medium")){
                    changeStyle(1);
                }else if(Objects.equals(value, "dark")){
                    changeStyle(2);
                }
            }else{
                //dynamic
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                firstMeasure = true;
            }


        }else {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            firstMeasure = true;
        }


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        int level = -1;
        if(sharedPref.contains(sharedPref_key)){
            level = sharedPref.getInt(sharedPref_key, -1);
        }
        if(type == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];
            Log.d("value", Float.toString(value));
            if(value < 5 && (level != 2 || firstMeasure)){
                changeStyle(2);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 2);
                editor.apply();
            }else if (value > 150 && (level != 0 || firstMeasure)){
                changeStyle(0);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 0);
                editor.apply();
            }else if (value < 150 && value > 5 && (level != 1 || firstMeasure)){
                changeStyle(1);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 1);
                editor.apply();
            }
            firstMeasure = false;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }

    private void changeStyle(int style){
        LinearLayout layout = findViewById(R.id.mainActLayout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        Button bSport = findViewById(R.id.bSport);
        Button bMusic = findViewById(R.id.bMusic);
        Button bArt = findViewById(R.id.bArt);
        Button bTeather = findViewById(R.id.bTheater);
        Button bCursos = findViewById(R.id.bCursos);

        switch (style){
            case 0:{
                layout.setBackgroundResource(R.color.light_background);
                myToolbar.setBackgroundResource(R.color.light_primary);

                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color));
                bCursos.setBackgroundColor(ContextCompat.getColor(this, R.color.cursos_color));

                break;
            }
            case 1:{
                layout.setBackgroundResource(R.color.medium_background);
                myToolbar.setBackgroundResource(R.color.medium_primary);

                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color));
                bCursos.setBackgroundColor(ContextCompat.getColor(this, R.color.cursos_color));

                break;

            }
            case 2:{

                layout.setBackgroundResource(R.color.dark_background);
                myToolbar.setBackgroundResource(R.color.dark_primary);

                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color_dark));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color_dark));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color_dark));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color_dark));
                bCursos.setBackgroundColor(ContextCompat.getColor(this, R.color.cursos_color_dark));


                break;

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.getIcon().setColorFilter(ContextCompat.getColor(this, R.color.dark_text), PorterDuff.Mode.SRC_IN);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent i;
        if(itemId == R.id.messages){
            i = new Intent(MainActivity.this, FavouritesListMQTT.class);
            startActivity(i);
            return true;
        }else if(itemId == R.id.settings){
            i = new Intent(MainActivity.this , SettingsActivity.class);
            startActivity(i);
            return true;
        }else if(itemId == R.id.stats){
            boolean internet_conn = InternetIsConnected();
            if(internet_conn){
                seeStatistics();
            }
            else{
                i = new Intent(MainActivity.this , InternetConnectionError.class);
                startActivity(i);
            }
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();

        setUpStyle();
    }

}
