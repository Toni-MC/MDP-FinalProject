package dte.masteriot.mdp.mdp_events_app;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    TextView title;
    private static final String TAG = "TAGListOfItems, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    void bindValues(Item item, Boolean isSelected) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        title.setText(item.getTitle());
        if(isSelected) {
            title.setTextColor(Color.BLUE);
        } else {
            title.setTextColor(Color.BLACK);
        }
    }

}