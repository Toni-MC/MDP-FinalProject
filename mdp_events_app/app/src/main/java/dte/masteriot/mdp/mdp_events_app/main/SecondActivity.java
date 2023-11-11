package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import dte.masteriot.mdp.mdp_events_app.R;

import dte.masteriot.mdp.mdp_events_app.roomDB.AppDatabase;
import dte.masteriot.mdp.mdp_events_app.roomDB.DatabaseClient;
import dte.masteriot.mdp.mdp_events_app.roomDB.Favourites;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback {
    AppDatabase db;
    Boolean favouriteSelected;


    long id;
    String title;
    String description;
    int is_free;
    String price;
    String dtstart;
    String dtend;
    String time;
    String link;
    String event_location;
    LatLng latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(""); //No title

        Intent intent = getIntent();

        id = intent.getLongExtra("id", 0);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        is_free = intent.getIntExtra("is_free", 0);
        price = intent.getStringExtra("price");
        dtstart = intent.getStringExtra("dtstart");
        dtend = intent.getStringExtra("dtend");
        time = intent.getStringExtra("time");
        link = intent.getStringExtra("link");
        event_location = intent.getStringExtra("event_location");
        latlng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));

        Log.d("Intent", title + " - " + is_free + " - " + price + " - " + dtstart + " - " + dtend + " - " + time + " - " + link + " - " + event_location);



        ImageButton favButton = findViewById(R.id.fav_button);
        favouriteSelected = false;
        db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        db.databaseWriteExecutor.execute(() -> {
            //check if the current event it is already followed by the user
            if(db.favouriteDao().getFavouriteByEventID(id).size() > 0){
                favouriteSelected = true;
                favButton.setImageResource(R.drawable.favorite_selected_45);
            }else{
                favouriteSelected = false;
                favButton.setImageResource(R.drawable.favourite_45);
            }
        });


        //Title
        TextView titleTv = findViewById(R.id.title);
        titleTv.setText(title);

        //Dates
        setDates(dtstart, dtend);

        // FALTA periocity
        //TO SET PEROCITY
        TextView t = findViewById(R.id.circulo_sabado);
        t.setBackgroundResource(R.drawable.circle_diselected);
        t = findViewById(R.id.circulo_domingo);
        t.setBackgroundResource(R.drawable.circle_diselected);

        //Location
        TextView location = findViewById(R.id.lugar_dato);
        location.setText(event_location);

        //Deploy map fragment
        showMap();

        //Set imgage
        ImageView picture = findViewById(R.id.picture);
        picture.setAdjustViewBounds(true);
        Picasso.get().load("https://www.madrid.es/UnidadesDescentralizadas/DistritoMoratalaz/Actividades/2023/octubre/exposicion345.jpg").into(picture);



        Button linkButton = findViewById(R.id.linkButton);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });


        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri gmmIntentUri = Uri.parse("geo:" + latlng.latitude + "," + latlng.longitude +"?q=" + latlng.latitude + "," + latlng.longitude + "Evento");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }


            }
        });


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favouriteSelected){
                    //the event was deselected
                    favButton.setImageResource(R.drawable.favourite_45);
                    favouriteSelected = false;
                    //delete from database as favourite event
                    AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                    db.databaseWriteExecutor.execute(() -> db.favouriteDao().deleteFavourite(id));


                }else{
                    //the event was selected
                    favouriteSelected = true;
                    favButton.setImageResource(R.drawable.favorite_selected_45);
                    //save on database as favourite
                    AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                    db.databaseWriteExecutor.execute(() -> db.favouriteDao().insert(new Favourites(id, title)));

                }
            }
        });

    }


    private String setDates(String dateTimeStart, String dateTimeEnd){

        SimpleDateFormat receiveFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String returnDate = "";
        try {
            Date dateStart = receiveFormat.parse(dateTimeStart);
            Date dateEnd = receiveFormat.parse(dateTimeEnd);
            if(dateStart.getDay() == dateEnd.getDay()){
                TextView fechaIniTitle = (TextView) findViewById(R.id.fechaIni);
                fechaIniTitle.setText(R.string.date);
                TextView fechaIni = (TextView) findViewById(R.id.fechaIni_dato);
                fechaIni.setText(simpleDate.format(dateStart));

                findViewById(R.id.fechaFin).setVisibility(View.GONE);
                findViewById(R.id.fechaFin_dato).setVisibility(View.GONE);

            }else{

                TextView fechaIni = (TextView) findViewById(R.id.fechaIni_dato);
                fechaIni.setText(simpleDate.format(dateStart));
                TextView fechaFin = (TextView) findViewById(R.id.fechaFin_dato);
                fechaFin.setText(simpleDate.format(dateEnd));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    private void showMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_map);
        supportMapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latlng).title("Marcador"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}