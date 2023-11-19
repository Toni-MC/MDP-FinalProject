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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import java.util.Objects;
import dte.masteriot.mdp.mdp_events_app.R;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Boolean firstMeasure;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelMainAct";

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
        actionBar.setTitle(""); //No title

        setUpStyle();





    }

    public void seeSportList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","sport");
        startActivity(i);
    }

    public void seeMusicList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","music");
        startActivity(i);
    }

    public void seeArtList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","art");
        startActivity(i);
    }

    public void seeTeatherList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","theater");
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
        Button bTeather = findViewById(R.id.bTeather);

        switch (style){
            case 0:{
                layout.setBackgroundResource(R.color.light_background);
                myToolbar.setBackgroundResource(R.color.light_primary);
                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color));

                break;
            }
            case 1:{
                layout.setBackgroundResource(R.color.medium_background);
                myToolbar.setBackgroundResource(R.color.medium_primary);
                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color));

                break;

            }
            case 2:{

                layout.setBackgroundResource(R.color.dark_background);
                myToolbar.setBackgroundResource(R.color.dark_primary);
                bSport.setBackgroundColor(ContextCompat.getColor(this, R.color.sport_color_dark));
                bMusic.setBackgroundColor(ContextCompat.getColor(this, R.color.music_color_dark));
                bArt.setBackgroundColor(ContextCompat.getColor(this, R.color.art_color_dark));
                bTeather.setBackgroundColor(ContextCompat.getColor(this, R.color.theater_color_dark));



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

        if(itemId == R.id.messages){
            Toast.makeText(this, "MESSAGES CLICKED", Toast.LENGTH_SHORT).show();
            return true;
        }else if(itemId == R.id.settings){
            Intent i = new Intent(MainActivity.this , SettingsActivity.class);
            startActivity(i);
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
