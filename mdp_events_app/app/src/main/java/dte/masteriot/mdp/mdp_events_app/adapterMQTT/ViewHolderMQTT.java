package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.ItemMQTT;


public class ViewHolderMQTT extends RecyclerView.ViewHolder {

    TextView EventName;
    CardView card;

    public ViewHolderMQTT(View itemView) {
        super(itemView);
        EventName = itemView.findViewById(R.id.EventName);
        card = itemView.findViewById(R.id.cardFavList);
    }

    void bindValues(ItemMQTT item, int style, Context context) {
        EventName.setText(item.getEventName());

        switch (style) {
            case 0: {
                EventName.setTextColor(ContextCompat.getColor(context, R.color.light_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_background));
                break;
            }
            case 1: {
                EventName.setTextColor(ContextCompat.getColor(context, R.color.medium_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.medium_background));

                break;

            }
            case 2: {
                EventName.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_card_background));

                break;

            }

        }

    }
}
