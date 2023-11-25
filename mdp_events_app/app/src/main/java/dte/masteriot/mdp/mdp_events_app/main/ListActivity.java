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
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Locale;
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

    private MyAdapter recyclerViewAdapter;
    private SelectionTracker<Long> tracker;
    private LoadingDialog loadingDialog;
    String json_str;
    ExecutorService es;
    private Dataset dataset = new Dataset();
    Handler handler;
    AsyncManager asyncManager;
    SharedPreferences sharedPref;
    String sharedPref_key = "lightLevelListAct";
    private Boolean firstMeasure;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    int currentTheme = 0;
    String date1;
    String date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        setUpStyle();

        asyncManager = new ViewModelProvider(this).get(AsyncManager.class);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                String string_result;

                super.handleMessage(msg);
                if((string_result = msg.getData().getString("text")) != null) {
                    json_str = string_result;
                    update_dataset();
                }
            }
        };

        if (savedInstanceState != null) {
            // Restore state related to selections previously made
            tracker.onRestoreInstanceState(savedInstanceState);
        }

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();

        //Default 7 days
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        date1 = sdf.format(c.getTime());
        c.add(Calendar.DATE, 7);
        date2 = sdf.format(c.getTime());

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
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

    }

    public void gridLayout(View view) {
        // Button to see in a grid fashion has been clicked:
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    void update_dataset(){
        Intent imputIntent = getIntent();
        String event_type = imputIntent.getStringExtra("event_type");
        dataset.date1 = date1;
        dataset.date2 = date2;
        dataset.event_type = event_type;
        dataset.construct_event_list(json_str);

        asyncManager.launchBackgroundTask(dataset);

        int max_limit;
        if(dataset.getSize() > 30){
            max_limit = 15;
        }
        else{
            max_limit = dataset.getSize();
        }
        Observer progressObserver = new Observer<Integer>(){
            @Override
            public void onChanged(Integer n_item) {
                loadingDialog.onContentChanged();
                if(recyclerView == null){
                    configure_recyclerview(dataset);
                }
                else {
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                if(n_item == (max_limit -1)){
                    loadingDialog.hide();
                }
            }
        };
        asyncManager.getProgress().observe(this, progressObserver);

        loadingDialog.onPreparePanel(dataset.getSize(), null, null);
        loadingDialog.show();

    }

    public void configure_recyclerview(Dataset dataset){
        // Prepare the RecyclerView:
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter.setContext(this);

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


    // Methods related to the SensorEventListener interface:
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        int level = -1;
        if(sharedPref.contains(sharedPref_key)){
            level = sharedPref.getInt(sharedPref_key, -1);
        }
        if(type == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];
            Log.d("value", "level: " + Float.toString(level));
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

    public void next7days(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        date1 = sdf.format(c.getTime());
        c.add(Calendar.DATE, 7);
        date2 = sdf.format(c.getTime());
        update_dataset();
    }

    public void next14days(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        date1 = sdf.format(c.getTime());
        c.add(Calendar.DATE, 14);
        date2 = sdf.format(c.getTime());
        update_dataset();
    }

    public void next30days(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        date1 = sdf.format(c.getTime());
        c.add(Calendar.DATE, 30);
        date2 = sdf.format(c.getTime());
        update_dataset();
    }

    public void selectDate(View view){


        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();


        if(currentTheme == 0){
            builder.setTheme(R.style.MaterialCalendarLight);
        }else if(currentTheme == 1){
            builder.setTheme(R.style.MaterialCalendarMedium);
        }else if(currentTheme == 2){
            builder.setTheme(R.style.MaterialCalendarDark);
        }

        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.setSelection(new Pair<>(
                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds()
        )).build();


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.first));
                date2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.second));
                update_dataset();
            }
        });

        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // In this app we do nothing if sensor's accuracy changes
    }


    private void changeStyle(int style){
        ConstraintLayout layout = findViewById(R.id.favActLayout);
        RecyclerView recycler = findViewById(R.id.recyclerView);
        Button selectdate = findViewById(R.id.selectdate);
        RadioButton bt7 = findViewById(R.id.bt7);
        RadioButton bt14 = findViewById(R.id.bt14);
        RadioButton bt30 = findViewById(R.id.bt30);
        //recyclerViewAdapter.notifyDataSetChanged();

        switch (style){
            case 0:{
                currentTheme = 0;
                if(recyclerView != null){ recyclerViewAdapter.changeStyle(style); }
                layout.setBackgroundResource(R.color.light_background);
                recycler.setBackgroundResource(R.color.light_background);
                selectdate.setBackgroundColor(ContextCompat.getColor(this, R.color.light_primary));
                bt7.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                bt14.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                bt30.setTextColor(ContextCompat.getColor(this, R.color.light_text));
                bt7.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_light));
                bt14.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_light));
                bt30.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_light));
                break;
            }
            case 1:{
                currentTheme = 1;
                if(recyclerView != null){ recyclerViewAdapter.changeStyle(style); }
                layout.setBackgroundResource(R.color.medium_background);
                recycler.setBackgroundResource(R.color.medium_background);
                selectdate.setBackgroundColor(ContextCompat.getColor(this, R.color.medium_primary));
                bt7.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                bt14.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                bt30.setTextColor(ContextCompat.getColor(this, R.color.medium_text));
                bt7.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_medium));
                bt14.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_medium));
                bt30.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_medium));
                break;

            }
            case 2:{
                currentTheme = 2;
                if(recyclerView != null){ recyclerViewAdapter.changeStyle(style); }
                layout.setBackgroundResource(R.color.dark_background);
                recycler.setBackgroundResource(R.color.dark_background);
                selectdate.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary));
                bt7.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                bt14.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                bt30.setTextColor(ContextCompat.getColor(this, R.color.dark_text));
                bt7.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_dark));
                bt14.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_dark));
                bt30.setButtonTintList(ContextCompat.getColorStateList(this, R.color.radio_button_color_dark));

                break;

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        setUpStyle();
    }


}