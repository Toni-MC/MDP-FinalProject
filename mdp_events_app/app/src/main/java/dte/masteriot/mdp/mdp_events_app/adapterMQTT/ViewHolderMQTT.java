package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.ItemMQTT;


public class ViewHolderMQTT extends RecyclerView.ViewHolder{

    TextView EventName;
    CardView card;
    public ViewHolderMQTT(View itemView) {
        super(itemView);
        EventName = itemView.findViewById(R.id.EventName);
        card= itemView.findViewById(R.id.cardFavList);
    }
    void bindValues(ItemMQTT item){
        EventName.setText(item.getEventName());
    }

}
