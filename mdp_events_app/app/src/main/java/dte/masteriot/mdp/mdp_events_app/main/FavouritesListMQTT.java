package dte.masteriot.mdp.mdp_events_app.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.AdapterMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.ItemDetailsLookupMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.ItemKeyProviderMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.OnItemActivatedListenerMQTT;
import dte.masteriot.mdp.mdp_events_app.model.DatasetMQTT;
import dte.masteriot.mdp.mdp_events_app.roomDB.*;

public class FavouritesListMQTT extends AppCompatActivity implements SensorEventListener {

    private RecyclerView recyclerView;

    private SelectionTracker<Long> tracker;

    private DatasetMQTT dataset = new DatasetMQTT();

    AppDatabase db;
    List<Favourites> FavouriteEvents;

    String username;

    TextView usernameText;
    TextView helperText;


    // style change
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Boolean firstMeasure;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelFavList";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list_mqtt);

        // Read favourites from RoomDB
        // Get every favourite by the user for them to select
        CompletableFuture<List<Favourites>> fav = new CompletableFuture<>();
        db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        db.databaseWriteExecutor.execute(() -> {
            List<Favourites> dbList=db.favouriteDao().getAllFavourites();

            Log.d("fav", "lista:" + dbList );

            if (dbList != null){
                fav.complete(dbList);
            }else {
                fav.complete(new ArrayList<Favourites>()); // empty list
            }

        });

        List<Favourites> FavouritesList= null;
        try {
            FavouritesList = fav.get();
        } catch (ExecutionException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        Log.d("fav2", "ID:" + FavouritesList.get(1).eventID + " Name" + FavouritesList.get(1).eventName);

        for (int i=0; i<FavouritesList.size(); i++){
            dataset.add(FavouritesList.get(i).eventID,FavouritesList.get(i).eventName);
        }

        // get username and uniqueID creation (if needed) from SharedPreferences instance
        SharedPreferences sharedPrefUser = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPrefUser.getString("user_name", null);
        Log.d("username", username);
        usernameText=findViewById(R.id.usernameText);
        usernameText.setText(username);

        helperText=findViewById(R.id.helperText);
        if (Objects.equals(username, "Anon")){
            helperText.setText("You can change your username on settings!");
        } else{
            helperText.setText("");
        }

        // Create unique ID if there isnt one
        if(!sharedPrefUser.contains("UniqueUserID")){
            sharedPrefUser.edit().putString("UniqueUserID", UUID.randomUUID().toString().substring(0,6)).apply();
        }


        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // toolbar
        Toolbar toolbar = findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Your favourite events"); //Topic name as title
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));


        //RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFavourites);
        AdapterMQTT recyclerViewAdapter = new AdapterMQTT(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        OnItemActivatedListenerMQTT onItemActivatedListenerMQTT = new OnItemActivatedListenerMQTT(this, dataset);
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new ItemKeyProviderMQTT(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new ItemDetailsLookupMQTT(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListenerMQTT)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);


        setUpStyle();
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

    }

    private void changeStyle(int style){

        ConstraintLayout layout = findViewById(R.id.favActLayout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.fav_toolbar);

        TextView usernameText = findViewById(R.id.usernameText);
        TextView helperText = findViewById(R.id.helperText);

        switch (style){
            case 0:{

                layout.setBackgroundResource(R.color.light_background);
                myToolbar.setBackgroundResource(R.color.light_primary);
                usernameText.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                helperText.setTextColor(ContextCompat.getColor(this, R.color.light_text));

                break;
            }
            case 1:{

                layout.setBackgroundResource(R.color.medium_background);
                myToolbar.setBackgroundResource(R.color.medium_primary);
                usernameText.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                helperText.setTextColor(ContextCompat.getColor(this, R.color.medium_text));


                break;

            }
            case 2:{

                layout.setBackgroundResource(R.color.dark_background);
                myToolbar.setBackgroundResource(R.color.dark_primary);
                usernameText.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                helperText.setTextColor(ContextCompat.getColor(this, R.color.dark_text));

                break;

            }
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();

        setUpStyle();
    }
}