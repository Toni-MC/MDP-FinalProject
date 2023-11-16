package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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


public class ListActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
}