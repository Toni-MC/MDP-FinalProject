package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

import dte.masteriot.mdp.mdp_events_app.main.MQTTMessaging;
import dte.masteriot.mdp.mdp_events_app.model.Dataset;
import dte.masteriot.mdp.mdp_events_app.main.SecondActivity;
import dte.masteriot.mdp.mdp_events_app.model.DatasetMQTT;

public class OnItemActivatedListenerMQTT implements OnItemActivatedListener<Long> {

    private static final String TAG = "TAGListOfItems, OnItemActivatedListenerMQTT";
    private final Context context;
    private DatasetMQTT dataset; // reference to the dataset, so that the activated item's data can be accessed if necessary

    public OnItemActivatedListenerMQTT(Context context, DatasetMQTT ds) {
        this.context = context;
        this.dataset = ds;
    }

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

        Intent i = new Intent(context, MQTTMessaging.class);
        i.putExtra("text", "Clicked item with position = " + itemdetails.getPosition()
                + " and key = " + itemdetails.getSelectionKey());
        i.putExtra("EventTitle", dataset.getItemAtPosition(itemdetails.getPosition()).getEventName());
        i.putExtra("EventID", dataset.getItemAtPosition(itemdetails.getPosition()).getEventID());

        context.startActivity(i);
        return true;
    }

}
