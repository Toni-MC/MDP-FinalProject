package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dte.masteriot.mdp.mdp_events_app.model.Dataset;
import dte.masteriot.mdp.mdp_events_app.adapter.MyAdapter;
import dte.masteriot.mdp.mdp_events_app.adapter.MyItemDetailsLookup;
import dte.masteriot.mdp.mdp_events_app.adapter.MyItemKeyProvider;
import dte.masteriot.mdp.mdp_events_app.adapter.MyOnItemActivatedListener;
import dte.masteriot.mdp.mdp_events_app.R;


public class ListActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "TAGListOfItems, MainActivity";

    private static final String URL_JSON = "https://datos.madrid.es/egob/catalogo/300107-0-agenda-actividades-eventos.json";
    private static final String  CONTENT_TYPE_JSON = "application/json";

    private RecyclerView recyclerView;
    private SelectionTracker<Long> tracker;
    String json_str;
    ExecutorService es;
    private Dataset dataset;
    Handler handler;
    AsyncManager asyncManager;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //CHECK IF DYNAMIC CONFIGURATION IS ON
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        asyncManager = new ViewModelProvider(this).get(AsyncManager.class);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                String string_result;

                super.handleMessage(msg);
                if((string_result = msg.getData().getString("text")) != null) {
                    json_str = string_result;
                    update_dataset(json_str);
                }
            }
        };

        if (savedInstanceState != null) {
            // Restore state related to selections previously made
            tracker.onRestoreInstanceState(savedInstanceState);
        }

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();

        update_events();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        tracker.onSaveInstanceState(outState); // Save state about selections.
    }

    // ------ Buttons' on-click listeners ------ //

    public void update_events() {

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_JSON);
        es.execute(loadURLContents);
    }

    public void set_item_images() {

        // Execute the loading task in background:
        LoadEventsImages loadEventsImages = new LoadEventsImages(handler, dataset);
        es.execute(loadEventsImages);
    }

    public void gridLayout(View view) {
        // Button to see in a grid fashion has been clicked:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void seeCurrentSelection(View view) {
        // Button "see current selection" has been clicked:

        Iterator<Long> iteratorSelectedItemsKeys = tracker.getSelection().iterator();
        // This iterator allows to navigate through the keys of the currently selected items.
        // Complete info on getSelection():
        // https://developer.android.com/reference/androidx/recyclerview/selection/SelectionTracker#getSelection()
        // Complete info on class Selection (getSelection() returns an object of this class):
        // https://developer.android.com/reference/androidx/recyclerview/selection/Selection

        String text = "";
        while (iteratorSelectedItemsKeys.hasNext()) {
            text += iteratorSelectedItemsKeys.next().toString();
            if (iteratorSelectedItemsKeys.hasNext()) {
                text += ", ";
            }
        }
        text = "Keys of currently selected items = \n" + text;
        Intent i = new Intent(this, SecondActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }

    void update_dataset(String json){
        Intent imputIntent = getIntent();
        String event_type = imputIntent.getStringExtra("event_type");
        dataset = new Dataset(json, event_type);
//        set_item_images();
        asyncManager.launchBackgroundTask(dataset);
        LoadingDialog loadingDialog = new LoadingDialog(this);

        int max_limit = dataset.getSize();
        if(max_limit>40){
            max_limit = max_limit/2; //to show the list when the half is loaded
        }
        int finalMax_limit = max_limit;
        Observer progressObserver = new Observer<Integer>(){
            @Override
            public void onChanged(Integer n_item) {
                loadingDialog.onContentChanged();
                if(n_item == (finalMax_limit - 1)){
                    loadingDialog.cancel();
                    configure_recyclerview(dataset);
                }
            }
        };
        asyncManager.getProgress().observe(this, progressObserver);

        loadingDialog.onPreparePanel(finalMax_limit, null, null);
        loadingDialog.show();

    }

    public void configure_recyclerview(Dataset dataset){
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Choose the layout manager to be set.
        // some options for the layout manager:  GridLayoutManager, LinearLayoutManager, StaggeredGridLayoutManager
        // by default, a linear layout is chosen:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Selection tracker (to allow for selection of items):
        MyOnItemActivatedListener onItemActivatedListener = new MyOnItemActivatedListener(this, dataset);
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, recyclerView),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);
    }


    // Methods related to the SensorEventListener interface:
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if(type == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];
            Log.d("value", Float.toString(value));
            if(value < 5){
                changeStyle(2);
            }else if (value > 150){
                changeStyle(0);
            }else{
                changeStyle(1);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }


    private void changeStyle(int style){
        ConstraintLayout layout = findViewById(R.id.FirstActLayout);
        RecyclerView recycler = findViewById(R.id.recyclerView);

        Button seeselectionbutton = findViewById(R.id.seeselectionbutton);
        Button update = findViewById(R.id.update);
        Button grid = findViewById(R.id.grid);
        //recyclerViewAdapter.notifyDataSetChanged();

        switch (style){
            case 0:{
                layout.setBackgroundResource(R.color.light_background);
                recycler.setBackgroundResource(R.color.light_background);
                seeselectionbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                update.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                grid.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                break;
            }
            case 1:{
                layout.setBackgroundResource(R.color.medium_background);
                recycler.setBackgroundResource(R.color.medium_background);
                seeselectionbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                update.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                grid.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                break;

            }
            case 2:{

                layout.setBackgroundResource(R.color.dark_background);
                recycler.setBackgroundResource(R.color.dark_background);
                seeselectionbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                update.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                grid.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                break;

            }
        }
    }


}