package dte.masteriot.mdp.mdp_events_app.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;

import dte.masteriot.mdp.mdp_events_app.R;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.stat_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Stats"); //No title
        myToolbar.setNavigationIcon(R.drawable.back_arrow);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        Intent intent = getIntent();

        int sport_value = intent.getIntExtra("n_sport", 0);
        int music_value = intent.getIntExtra("n_music", 0);
        int art_value = intent.getIntExtra("n_art", 0);
        int theater_value = intent.getIntExtra("n_theater", 0);
        int other_value = intent.getIntExtra("n_other", 0);
        int cursos_value = intent.getIntExtra("n_courses", 0);

        TextView tv = findViewById(R.id.stat_sport_value);
        tv.setText(Integer.toString(sport_value));
        tv = findViewById(R.id.stat_music_value);
        tv.setText(Integer.toString(music_value));
        tv = findViewById(R.id.stat_art_value);
        tv.setText(Integer.toString(art_value));
        tv = findViewById(R.id.stat_theater_value);
        tv.setText(Integer.toString(theater_value));
        tv = findViewById(R.id.stat_cursos_value);
        tv.setText(Integer.toString(cursos_value));
        tv = findViewById(R.id.stat_other_value);
        tv.setText(Integer.toString(other_value));

        final float fontSize = PixelUtils.spToPix(23);

        PieChart pieChart = (PieChart) findViewById(R.id.plot);

        // Set up sample data for the chart
        Segment s1 = new Segment("Sport", sport_value);
        Segment s2 = new Segment("Music", music_value);
        Segment s3 = new Segment("Art", art_value);
        Segment s4 = new Segment("Theater", theater_value);
        Segment s5 = new Segment("Workshop", other_value);
        Segment s6 = new Segment("Other", other_value);

        // Add segments to the chart
        SegmentFormatter sf1 = new SegmentFormatter(ContextCompat.getColor(this, R.color.sport_color));
        sf1.getLabelPaint().setTextSize(fontSize);
        sf1.getLabelPaint().setFakeBoldText(true);
        SegmentFormatter sf2 = new SegmentFormatter(ContextCompat.getColor(this, R.color.music_color));
        sf2.getLabelPaint().setTextSize(fontSize);
        sf2.getLabelPaint().setFakeBoldText(true);
        SegmentFormatter sf3 = new SegmentFormatter(ContextCompat.getColor(this, R.color.art_color));
        sf3.getLabelPaint().setTextSize(fontSize);
        sf3.getLabelPaint().setFakeBoldText(true);
        SegmentFormatter sf4 = new SegmentFormatter(ContextCompat.getColor(this, R.color.theater_color));
        sf4.getLabelPaint().setTextSize(fontSize);
        sf4.getLabelPaint().setFakeBoldText(true);
        SegmentFormatter sf5 = new SegmentFormatter(ContextCompat.getColor(this, R.color.cursos_color_dark));
        sf5.getLabelPaint().setTextSize(fontSize);
        sf5.getLabelPaint().setFakeBoldText(true);
        SegmentFormatter sf6 = new SegmentFormatter(ContextCompat.getColor(this, R.color.grey));
        sf6.getLabelPaint().setTextSize(fontSize);
        sf6.getLabelPaint().setFakeBoldText(true);

        pieChart.addSegment(s1, sf1);
        pieChart.addSegment(s2, sf2);
        pieChart.addSegment(s3, sf3);
        pieChart.addSegment(s4, sf4);
        pieChart.addSegment(s5, sf5);
        pieChart.addSegment(s6, sf6);
        // Customize the chart
        pieChart.setTitle("");


        // Refresh the chart to display the updated data
        pieChart.redraw();

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
