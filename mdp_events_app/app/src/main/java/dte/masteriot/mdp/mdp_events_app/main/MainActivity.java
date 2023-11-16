package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.Iterator;
import java.util.concurrent.Executors;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.model.Dataset;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

    }

    public void seeSportList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeMusicList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeArtList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
//        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }

    public void seeTeatherList(View v){
        Intent i = new Intent(MainActivity.this , ListActivity.class);
//        i.putExtra("event_type","deporte");
//        i.putExtra("event_type","expo_arte");
//        i.putExtra("event_type","musica");
        i.putExtra("event_type","teatro_av");
        startActivity(i);
    }


}
