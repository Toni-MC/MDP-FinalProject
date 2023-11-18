package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.concurrent.Executors;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.Dataset;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelMainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(""); //No title


        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //CHECK IF DYNAMIC CONFIGURATION IS ON
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void seeSportList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeMusicList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeArtList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeTeatherList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
        i.putExtra("event_type","teatro_av");
        startActivity(i);
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
            if(value < 5 && level != 2){
                changeStyle(2);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 2);
                editor.apply();
            }else if (value > 150 && level !=0){
                changeStyle(0);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 0);
                editor.apply();
            }else if (value < 150 && value > 5 && level != 1){
                changeStyle(1);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 1);
                editor.apply();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }

    private void changeStyle(int style){
        LinearLayout layout = findViewById(R.id.mainActLayout);
        //recyclerViewAdapter.notifyDataSetChanged();

        switch (style){
            case 0:{
                layout.setBackgroundResource(R.color.light_background);
                break;
            }
            case 1:{
                layout.setBackgroundResource(R.color.medium_background);
                break;

            }
            case 2:{

                layout.setBackgroundResource(R.color.dark_background);
                break;

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
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



}
