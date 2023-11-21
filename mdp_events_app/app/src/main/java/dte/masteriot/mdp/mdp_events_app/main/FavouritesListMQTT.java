package dte.masteriot.mdp.mdp_events_app.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.AdapterMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.ItemDetailsLookupMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.ItemKeyProviderMQTT;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.OnItemActivatedListenerMQTT;
import dte.masteriot.mdp.mdp_events_app.model.DatasetMQTT;
import dte.masteriot.mdp.mdp_events_app.roomDB.*;

public class FavouritesListMQTT extends AppCompatActivity {

    private RecyclerView recyclerView;

    private SelectionTracker<Long> tracker;

    private DatasetMQTT dataset = new DatasetMQTT(1);

    AppDatabase db;
    List<Favourites> FavouriteEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list_mqtt);

        // Read favourites from RoomDB
        // Get every favourite by the user for them to select
        db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        db.databaseWriteExecutor.execute(() -> {
            //if there is at least 1 favourite, get the full list
            if(db.favouriteDao().getAllFavourites().size() > 0){
                FavouriteEvents=db.favouriteDao().getAllFavourites();
            }else{
                // no favourites found
            }
        });
//        dataset.add(FavouriteEvents.get(1).eventID,FavouriteEvents.get(1).eventName);
//        dataset.add(FavouriteEvents.get(2).eventID,FavouriteEvents.get(2).eventName);
//        dataset.add(FavouriteEvents.get(3).eventID,FavouriteEvents.get(3).eventName);
//        dataset.add(FavouriteEvents.get(4).eventID,FavouriteEvents.get(4).eventName);



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
    }
}