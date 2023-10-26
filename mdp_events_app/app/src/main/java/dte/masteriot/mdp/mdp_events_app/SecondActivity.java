package dte.masteriot.mdp.mdp_events_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback {


    LatLng latLng;
    String link;
    String lugar;
    Boolean favouriteSelected;

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

        favouriteSelected = false;

        //COMPROBAR SI ESTA ACTIVIDAD YA FUE ANTERIORMENTE SELECCIONADA -> PETICIÓN A ROOM CON EL ID

        //TO SET PEROCITY
        TextView t = findViewById(R.id.circulo_sabado);
        t.setBackgroundResource(R.drawable.circle_diselected);
        t = findViewById(R.id.circulo_domingo);
        t.setBackgroundResource(R.drawable.circle_diselected);


        String dtStart = "2023-12-15T09:27:37Z";
        TextView fechaIni = (TextView) findViewById(R.id.fechaIni_dato);
        fechaIni.setText(getDate(dtStart));

        lugar = "ETSIST, Campus sur UPM";
        link = "https://www.etsist.upm.es/";
        latLng = new LatLng(40.389717, -3.629415);


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


                Uri gmmIntentUri = Uri.parse("geo:" + latLng.latitude + "," + latLng.longitude +"?q=" + latLng.latitude + "," + latLng.longitude + "Evento");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }


            }
        });

        ImageButton favButton = findViewById(R.id.fav_button);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favouriteSelected){
                    favButton.setImageResource(R.drawable.favourite_45);
                    favouriteSelected = false;
                }else{
                    favouriteSelected = true;
                    favButton.setImageResource(R.drawable.favorite_selected_45);
                }
            }
        });





        showMap();
    }


    private String getDate(String dateTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String returnDate = "";
        try {
            Date date = format.parse(dateTime);
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy - hh:mm");
            returnDate = simpleDate.format(date); // return in new format

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
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marcador"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
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