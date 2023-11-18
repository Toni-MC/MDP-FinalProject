package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import dte.masteriot.mdp.mdp_events_app.R;

import dte.masteriot.mdp.mdp_events_app.roomDB.AppDatabase;
import dte.masteriot.mdp.mdp_events_app.roomDB.DatabaseClient;
import dte.masteriot.mdp.mdp_events_app.roomDB.Favourites;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    AppDatabase db;
    Boolean favouriteSelected;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Boolean firstMeasure;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelSecondAct";



    long id;
    String title;
    String description;
    int is_free;
    String price;
    String dtstart;
    String dtend;
    String time;
    String link;
    String event_location;
    LatLng latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(""); //No title

        Intent intent = getIntent();

        id = intent.getLongExtra("id", 0);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        is_free = intent.getIntExtra("is_free", 0);
        price = intent.getStringExtra("price");
        dtstart = intent.getStringExtra("dtstart");
        dtend = intent.getStringExtra("dtend");
        time = intent.getStringExtra("time");
        link = intent.getStringExtra("link");
        event_location = intent.getStringExtra("event_location");
        latlng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));

        Log.d("Intent", title + " - " + is_free + " - " + price + " - " + dtstart + " - " + dtend + " - " + time + " - " + link + " - " + event_location);



        ImageButton favButton = findViewById(R.id.fav_button);
        favouriteSelected = false;
        db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        db.databaseWriteExecutor.execute(() -> {
            //check if the current event it is already followed by the user
            if(db.favouriteDao().getFavouriteByEventID(id).size() > 0){
                favouriteSelected = true;
                favButton.setImageResource(R.drawable.favorite_selected_45);
            }else{
                favouriteSelected = false;
                favButton.setImageResource(R.drawable.favourite_45);
            }
        });


        //Title
        TextView titleTv = findViewById(R.id.titleEvent);
        titleTv.setText(title);

        //Dates
        setDates(dtstart, dtend);

        // FALTA periocity
        //TO SET PEROCITY
        TextView t = findViewById(R.id.circulo_sabado);
        t.setBackgroundResource(R.drawable.circle_diselected);
        t = findViewById(R.id.circulo_domingo);
        t.setBackgroundResource(R.drawable.circle_diselected);

        //Location
        TextView location = findViewById(R.id.lugar_dato);
        location.setText(event_location);

        //Deploy map fragment
        showMap();

        //Set imgage
        ImageView picture = findViewById(R.id.picture);
        picture.setAdjustViewBounds(true);
        Picasso.get().load("https://www.madrid.es/UnidadesDescentralizadas/DistritoMoratalaz/Actividades/2023/octubre/exposicion345.jpg").into(picture);



        Button linkButton = findViewById(R.id.linkButton);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });


        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri gmmIntentUri = Uri.parse("geo:" + latlng.latitude + "," + latlng.longitude +"?q=" + latlng.latitude + "," + latlng.longitude + "Evento");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }


            }
        });


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favouriteSelected){
                    //the event was deselected
                    favButton.setImageResource(R.drawable.favourite_45);
                    favouriteSelected = false;
                    //delete from database as favourite event
                    AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                    db.databaseWriteExecutor.execute(() -> db.favouriteDao().deleteFavourite(id));

                }else{
                    //the event was selected
                    favouriteSelected = true;
                    favButton.setImageResource(R.drawable.favorite_selected_45);
                    //save on database as favourite
                    AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                    db.databaseWriteExecutor.execute(() -> db.favouriteDao().insert(new Favourites(id, title)));

                }
            }
        });


        setUpStyle();
    }


    private String setDates(String dateTimeStart, String dateTimeEnd){

        SimpleDateFormat receiveFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String returnDate = "";
        try {
            Date dateStart = receiveFormat.parse(dateTimeStart);
            Date dateEnd = receiveFormat.parse(dateTimeEnd);
            if(dateStart.getDay() == dateEnd.getDay()){
                TextView fechaIniTitle = (TextView) findViewById(R.id.fechaIni);
                fechaIniTitle.setText(R.string.date);
                TextView fechaIni = (TextView) findViewById(R.id.fechaIni_dato);
                fechaIni.setText(simpleDate.format(dateStart));

                findViewById(R.id.fechaFin).setVisibility(View.GONE);
                findViewById(R.id.fechaFin_dato).setVisibility(View.GONE);

            }else{

                TextView fechaIni = (TextView) findViewById(R.id.fechaIni_dato);
                fechaIni.setText(simpleDate.format(dateStart));
                TextView fechaFin = (TextView) findViewById(R.id.fechaFin_dato);
                fechaFin.setText(simpleDate.format(dateEnd));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    private void showMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_map);
        supportMapFragment.getMapAsync(this);

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

        LinearLayout layout = findViewById(R.id.secondActLayout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        ScrollView scroll = findViewById(R.id.secondActScroll);

        TextView title = findViewById(R.id.titleEvent);
        TextView activityType = findViewById(R.id.activityType);
        TextView fechaIni = findViewById(R.id.fechaIni);
        TextView fechaIni_dato = findViewById(R.id.fechaIni_dato);
        TextView fechaFin = findViewById(R.id.fechaFin);
        TextView fechaFin_dato = findViewById(R.id.fechaFin_dato);
        TextView periocidad = findViewById(R.id.periocidad);
        TextView lugar = findViewById(R.id.lugar);
        TextView lugar_dato = findViewById(R.id.lugar_dato);

        Button mapButton = findViewById(R.id.mapButton);
        Button linkButton = findViewById(R.id.linkButton);

        switch (style){

            case 0:{
                layout.setBackgroundResource(R.color.light_background);
                myToolbar.setBackgroundResource(R.color.light_primary);
                scroll.setBackgroundResource(R.color.light_background);

                title.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                activityType.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                fechaIni.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                fechaIni_dato.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                fechaFin.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                fechaFin_dato.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                periocidad.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                lugar.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                lugar_dato.setTextColor(ContextCompat.getColor(this, R.color.light_text));

                mapButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                linkButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                break;
            }
            case 1:{
                layout.setBackgroundResource(R.color.medium_background);
                myToolbar.setBackgroundResource(R.color.medium_primary);
                scroll.setBackgroundResource(R.color.medium_background);

                title.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                activityType.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                fechaIni.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                fechaIni_dato.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                fechaFin.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                fechaFin_dato.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                periocidad.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                lugar.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                lugar_dato.setTextColor(ContextCompat.getColor(this, R.color.medium_text));

                mapButton.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                linkButton.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                break;

            }
            case 2:{
                layout.setBackgroundResource(R.color.dark_background);
                myToolbar.setBackgroundResource(R.color.dark_primary);
                scroll.setBackgroundResource(R.color.dark_background);

                title.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                activityType.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                fechaIni.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                fechaIni_dato.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                fechaFin.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                fechaFin_dato.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                periocidad.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                lugar.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                lugar_dato.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

                mapButton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                linkButton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                break;

            }



        }



    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latlng).title("Marcador"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUpStyle();
    }

}