package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

public class ItemKeyProviderMQTT extends ItemKeyProvider<Long> {


    RecyclerView recView;

    public ItemKeyProviderMQTT(int scope, RecyclerView rv) {
        super(scope);
        recView = rv;
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public Long getKey(int position) {
        return (((AdapterMQTT) recView.getAdapter()).getKeyAtPosition(position));
    }

    @SuppressLint("LongLogTag")
    @Override
    public int getPosition(@NonNull Long key) {
        return (((AdapterMQTT) recView.getAdapter()).getPositionOfKey(key));
    }
}
