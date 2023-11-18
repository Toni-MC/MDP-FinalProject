package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


import java.util.Objects;

import dte.masteriot.mdp.mdp_events_app.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setUpStyle();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);



        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Settings"); //No title


    }


    private void setUpStyle(){
        SharedPreferences configPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(configPref.contains("static_theme")){
            Boolean selected = configPref.getBoolean("static_theme", false);
            String value = configPref.getString("static_theme_selected", null);

            if(selected){
                changeStyle(value);
            }else{
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
                int level = sharedPref.getInt("lightLevelMainAct", -1);

                if(level == 0){
                    changeStyle("light");
                }else if(level == 1){
                    changeStyle("medium");
                }else if(level == 2){
                    changeStyle("dark");
                }

            }


        }else {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
            int level = sharedPref.getInt("lightLevelMainAct", -1);

            if(level == 0){
                changeStyle("light");
            }else if(level == 1){
                changeStyle("medium");
            }else if(level == 2){
                changeStyle("dark");
            }
        }
    }

    private void changeStyle(String value){

        if(Objects.equals(value, "light")){
            setTheme(R.style.Theme_AppThemeMain);
        }else if(Objects.equals(value, "medium")){
            setTheme(R.style.Theme_AppThemeSecondary);
        }else if(Objects.equals(value, "dark")){
            setTheme(R.style.Theme_AppThemeTertiary);
        }

    }





    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
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