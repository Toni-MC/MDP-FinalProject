package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.adapter.MyViewHolder;
import dte.masteriot.mdp.mdp_events_app.model.Dataset;
import dte.masteriot.mdp.mdp_events_app.model.DatasetMQTT;
import dte.masteriot.mdp.mdp_events_app.model.ItemMQTT;

public class AdapterMQTT extends RecyclerView.Adapter<ViewHolderMQTT> {

    private static final String TAG = "TAGListOfItems, MyAdapterMQTT";
    private  final DatasetMQTT dataset;
    private SelectionTracker<Long> selectionTracker;

    public AdapterMQTT(DatasetMQTT dataset) {
        super();
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolderMQTT onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mqtt, parent, false);
        return new ViewHolderMQTT(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderMQTT holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final ItemMQTT item = dataset.getItemAtPosition(position);
        Long itemKey = item.getEventID();

        Log.d(TAG, "onBindViewHolder() called for element in position " + position);
        holder.bindValues(item);
    }

    @Override
    public int getItemCount() {
        return dataset.getSize();
    }
    public Long getKeyAtPosition(int pos) {
        return (dataset.getIDAtPosition(pos));
    }

    public String getEventNameAtPosition(int pos){
        return (dataset.getNameAtPosition(pos));
    }

    public int getPositionOfKey(Long searchedkey) {
        //Log.d(TAG, "getPositionOfKey() called for key " + searchedkey + ", returns " + position);
        int position = dataset.getPositionOfKey(searchedkey);
        return position;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }


}
