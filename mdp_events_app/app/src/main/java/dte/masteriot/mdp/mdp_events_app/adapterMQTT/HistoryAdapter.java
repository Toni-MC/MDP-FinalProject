package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.HistoryItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> history;

    public int currentStyle;
    // 0 light
    // 1 medium
    // 2 dark

    private Context context;

    public HistoryAdapter(ArrayList<HistoryItem> dataSet) {
        history = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);

        return new ViewHolder(view);
    }

    public void addMsg(String msg, String clientID, String clientUsername, String timestamp){
        history.add(new HistoryItem(clientID,clientUsername,msg,timestamp));
        this.notifyDataSetChanged();
    }

    public void addSYSTEM(String msg, String timestamp) {
        history.add(new HistoryItem("------------------", "SYSTEM", msg, timestamp));
        this.notifyDataSetChanged();
    }

    public void changeStyle(int style){
        currentStyle=style;
        this.notifyDataSetChanged();
    }

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msgUsername.setText(history.get(position).getClientUsername());
        holder.msgUserID.setText(history.get(position).getClientID());
        holder.msgText.setText(history.get(position).getMsg());
        holder.timestamp.setText(history.get(position).getTimestampLocal());

        switch (currentStyle) {
            case 0: {

                holder.msgUsername.setTextColor(ContextCompat.getColor(context, R.color.light_text));
                holder.msgUserID.setTextColor(ContextCompat.getColor(context, R.color.light_text));
                holder.msgText.setTextColor(ContextCompat.getColor(context, R.color.light_text));
                holder.timestamp.setTextColor(ContextCompat.getColor(context, R.color.light_text));

                break;
            }
            case 1: {

                holder.msgUsername.setTextColor(ContextCompat.getColor(context, R.color.medium_text));
                holder.msgUserID.setTextColor(ContextCompat.getColor(context, R.color.medium_text));
                holder.msgText.setTextColor(ContextCompat.getColor(context, R.color.medium_text));
                holder.timestamp.setTextColor(ContextCompat.getColor(context, R.color.medium_text));


                break;

            }
            case 2: {
                holder.msgUsername.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                holder.msgUserID.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                holder.msgText.setTextColor(ContextCompat.getColor(context, R.color.dark_text));
                holder.timestamp.setTextColor(ContextCompat.getColor(context, R.color.dark_text));


                break;

            }
        }


    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView msgText;
        public TextView msgUsername,msgUserID;
        public TextView timestamp;



        public ViewHolder(View view) {
            super(view);
            msgText = view.findViewById(R.id.msgText);
            msgUsername= view.findViewById(R.id.msgUsername);
            msgUserID= view.findViewById(R.id.msgUserID);
            timestamp= view.findViewById(R.id.timestamp);

        }
    }

}



