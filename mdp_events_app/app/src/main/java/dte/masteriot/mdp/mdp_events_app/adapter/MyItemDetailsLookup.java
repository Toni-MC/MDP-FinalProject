package dte.masteriot.mdp.mdp_events_app.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

public final class MyItemDetailsLookup extends ItemDetailsLookup<Long> {

    // From [https://developer.android.com/reference/androidx/recyclerview/selection/ItemDetailsLookup]:
    // The Selection library calls the method getItemDetails(MotionEvent) of an object of this class
    // when it needs access to information about the area and/or ItemDetailsLookup.ItemDetails
    // under a MotionEvent

    private static final String TAG = "TAGListOfItems, MyItemDetailsLookup";

    private final RecyclerView mRecyclerView;

    @SuppressLint("LongLogTag")
    public MyItemDetailsLookup(RecyclerView recyclerView) {
        Log.d(TAG, "MyItemDetailsLookup() called");
        mRecyclerView = recyclerView;
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        Log.d(TAG, "getItemDetails() called for a given MotionEvent");
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof MyViewHolder) {
                int positionOfTheHolder = holder.getAbsoluteAdapterPosition();
                Long keyOfTheHolder = ((MyAdapter) holder.getBindingAdapter()).getKeyAtPosition(positionOfTheHolder);

                ItemDetails<Long> itemDetails = new ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return (positionOfTheHolder);
                    }
                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return (keyOfTheHolder);
                    }
                };

                return itemDetails;
            }
        }
        return null;
    }

}