package dte.masteriot.mdp.mdp_events_app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import dte.masteriot.mdp.mdp_events_app.model.Item;
import dte.masteriot.mdp.mdp_events_app.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    TextView title;
    ImageView image;
    TextView is_free;

    CardView card;
    private static final String TAG = "TAGListOfItems, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image_view);
        is_free = itemView.findViewById(R.id.is_free);
        card = itemView.findViewById(R.id.cardList);
    }

    void bindValues(Item item, Boolean isSelected, int style, Context context) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        title.setText(item.getTitle());
        image.setAdjustViewBounds(true);
        if(item.getIs_free() == 1){
            is_free.setText("Free");
            is_free.setTextColor(Color.parseColor("#047A08"));
        }
        else {
            is_free.setText("");
        }

        if(item.getImageLink() == "Default"){
            switch (item.getType()){
                case "sport":
                    Picasso.get().load(R.drawable.sport_icon).resize(130,130).into(image);
                    break;
                case "art":
                    Picasso.get().load(R.drawable.arte_icon).resize(130,130).into(image);
                    break;
                case "music":
                    Picasso.get().load(R.drawable.microfono_icons).resize(130,130).into(image);
                    break;
                case "theater":
                    Picasso.get().load(R.drawable.teatro_icon).resize(130,130).into(image);
                    break;
                case "courses":
                    Picasso.get().load(R.drawable.curso_icon).resize(130,130).into(image);
                    break;
                case "other":
                    Picasso.get().load(R.drawable.otro_icon).resize(130,130).into(image);
                    break;
                default:
                    break;
            }

        }
        else{
            Picasso.get().load(item.getImageLink()).resize(130,130).into(image);
        }


        if(isSelected) {
            title.setTextColor(Color.BLUE);
        } else {
            title.setTextColor(Color.BLACK);
        }


        switch (style) {
            case 0: {
                title.setTextColor(ContextCompat.getColor(context, R.color.light_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_background));
                break;
            }
            case 1: {
                title.setTextColor(ContextCompat.getColor(context, R.color.medium_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.medium_background));
                break;
            }
            case 2: {
                title.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_card_background));
                break;
            }

        }

    }

}