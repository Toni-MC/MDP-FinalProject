package dte.masteriot.mdp.mdp_events_app.adapterMQTT;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.HistoryItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> history;

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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msgUsername.setText(history.get(position).getClientUsername());
        holder.msgUserID.setText(history.get(position).getClientID());
        holder.msgText.setText(history.get(position).getMsg());
        holder.timestamp.setText(history.get(position).getTimestampLocal());
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



