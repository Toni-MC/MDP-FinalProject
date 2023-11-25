package dte.masteriot.mdp.mdp_events_app.main;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import dte.masteriot.mdp.mdp_events_app.R;

public class InternetConnectionError extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_connection_error);
        // toolbar
        Toolbar toolbar = findViewById(R.id.internet_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Connection Error"); //Topic name as title
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

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
