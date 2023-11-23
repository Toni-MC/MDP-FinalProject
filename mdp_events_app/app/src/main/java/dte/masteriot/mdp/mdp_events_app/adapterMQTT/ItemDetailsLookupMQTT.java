package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;



public class ItemDetailsLookupMQTT extends ItemDetailsLookup<Long> {

    private final RecyclerView mRecyclerView;

    public ItemDetailsLookupMQTT(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof ViewHolderMQTT) {
                int positionOfTheHolder = holder.getAbsoluteAdapterPosition();
                Long keyOfTheHolder = ((AdapterMQTT) holder.getBindingAdapter()).getKeyAtPosition(positionOfTheHolder);

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
