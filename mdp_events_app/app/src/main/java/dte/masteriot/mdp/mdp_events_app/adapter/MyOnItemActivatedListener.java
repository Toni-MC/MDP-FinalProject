package dte.masteriot.mdp.mdp_events_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

import com.google.android.gms.maps.model.LatLng;

import dte.masteriot.mdp.mdp_events_app.model.Dataset;
import dte.masteriot.mdp.mdp_events_app.main.SecondActivity;

public class MyOnItemActivatedListener implements OnItemActivatedListener<Long> {

    // This class serves to "Register an OnItemActivatedListener to be notified when an item
    // is activated (tapped or double clicked)."
    // [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]

    private static final String TAG = "TAGListOfItems, MyOnItemActivatedListener";

    private final Context context;
    private Dataset dataset; // reference to the dataset, so that the activated item's data can be accessed if necessary

    public MyOnItemActivatedListener(Context context, Dataset ds) {
        this.context = context;
        this.dataset = ds;
    }

    // ------ Implementation of methods ------ //

    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails itemdetails,
                                   @NonNull MotionEvent e) {
        // From [https://developer.android.com/reference/androidx/recyclerview/selection/OnItemActivatedListener]:
        // "Called when an item is "activated". An item is activated, for example,
        // when no selection exists and the user taps an item with her finger,
        // or double clicks an item with a pointing device like a Mouse."

        Log.d(TAG, "Clicked item with position = " + itemdetails.getPosition()
                + " and key = " + itemdetails.getSelectionKey());

        Intent i = new Intent(context, SecondActivity.class);
        i.putExtra("text", "Clicked item with position = " + itemdetails.getPosition()
                + " and key = " + itemdetails.getSelectionKey());
        i.putExtra("title", dataset.getItemAtPosition(itemdetails.getPosition()).getTitle());
        i.putExtra("descriptrion", dataset.getItemAtPosition(itemdetails.getPosition()).getDescription());
        i.putExtra("is_free", dataset.getItemAtPosition(itemdetails.getPosition()).getIs_free());
        i.putExtra("price", dataset.getItemAtPosition(itemdetails.getPosition()).getPrice());
        i.putExtra("dtstart", dataset.getItemAtPosition(itemdetails.getPosition()).getDtstart());
        i.putExtra("dtend", dataset.getItemAtPosition(itemdetails.getPosition()).getDtend());
        i.putExtra("time", dataset.getItemAtPosition(itemdetails.getPosition()).getTime());
        i.putExtra("link", dataset.getItemAtPosition(itemdetails.getPosition()).getLink());
        i.putExtra("event_location", dataset.getItemAtPosition(itemdetails.getPosition()).getEvent_location());
        i.putExtra("latitude", dataset.getItemAtPosition(itemdetails.getPosition()).getLatlng().latitude);
        i.putExtra("longitude", dataset.getItemAtPosition(itemdetails.getPosition()).getLatlng().longitude);
        i.putExtra("id", dataset.getItemAtPosition(itemdetails.getPosition()).getKey());
        i.putExtra("periocity", dataset.getItemAtPosition(itemdetails.getPosition()).getRecurrence());
        i.putExtra("imageLink", dataset.getItemAtPosition(itemdetails.getPosition()).getImage_link());
        i.putExtra("entireType", dataset.getItemAtPosition(itemdetails.getPosition()).getEntireType());
        i.putExtra("type", dataset.getItemAtPosition(itemdetails.getPosition()).getType());



        context.startActivity(i);
        return true;
    }
}
