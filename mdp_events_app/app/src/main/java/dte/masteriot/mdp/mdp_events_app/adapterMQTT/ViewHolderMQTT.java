package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.ItemMQTT;


public class ViewHolderMQTT extends RecyclerView.ViewHolder{

    TextView EventName;
    public ViewHolderMQTT(View itemView) {
        super(itemView);
        EventName = itemView.findViewById(R.id.EventName);
    }
    void bindValues(ItemMQTT item){
        EventName.setText(item.getEventName());
    }
}
