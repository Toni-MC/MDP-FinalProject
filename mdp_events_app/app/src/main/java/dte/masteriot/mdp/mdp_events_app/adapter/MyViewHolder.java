package dte.masteriot.mdp.mdp_events_app.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import dte.masteriot.mdp.mdp_events_app.model.Item;
import dte.masteriot.mdp.mdp_events_app.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    TextView title;
    ImageView image;
    TextView is_free;
    private static final String TAG = "TAGListOfItems, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image_view);
        is_free = itemView.findViewById(R.id.is_free);
    }

    void bindValues(Item item, Boolean isSelected) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        title.setText(item.getTitle());
        image.setAdjustViewBounds(true);
        if(item.getIs_free() == 1){
            is_free.setText("Free");
            is_free.setTextColor(Color.GREEN);
        }
        else {
            is_free.setText(item.getPrice());
        }

        if(item.getImageLink() == "Default"){
            switch (item.getType()){
                case "sport":
                    Picasso.get().load(R.drawable.sport).resize(130,130).into(image);
                    break;
                case "art":
                    Picasso.get().load(R.drawable.art).resize(130,130).into(image);
                    break;
                case "music":
                    Picasso.get().load(R.drawable.music).resize(130,130).into(image);
                    break;
                case "theater":
                    Picasso.get().load(R.drawable.theater).resize(130,130).into(image);
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
    }

}