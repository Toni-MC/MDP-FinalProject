package dte.masteriot.mdp.mdp_events_app.main;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import dte.masteriot.mdp.mdp_events_app.R;
import dte.masteriot.mdp.mdp_events_app.adapterMQTT.HistoryAdapter;
import dte.masteriot.mdp.mdp_events_app.model.HistoryItem;
import dte.masteriot.mdp.mdp_events_app.roomDB.*;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MQTTMessaging extends AppCompatActivity implements SensorEventListener {

    // public hiveMQ broker:
    final String serverUri= "tcp://broker.hivemq.com:1883";

    // final String serverUri= "tcp://broker.emqx.io:1883";
    // final String serverUri = "tcp://iot.eclipse.org:1883";
    // final String serverUri = "tcp://192.168.56.1:1883";
    String publishTopic;
    Long eventID;
    String eventName;
    MqttAndroidClient mqttAndroidClient;
    String clientUsername;
    String clientId;
    private HistoryAdapter mAdapter;

    EditText msgToSend;
    SharedPreferences sharedPref;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Boolean firstMeasure;
    String sharedPref_key = "lightLevelMQTTMess";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        eventID = intent.getLongExtra("EventID", 0);
        eventName = intent.getStringExtra("EventTitle");

        publishTopic=eventID.toString();
        //publishTopic="Test123";


        setContentView(R.layout.activity_mqttmessaging);

        Toolbar toolbar = findViewById(R.id.mqttToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(eventName); //Topic name as title
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        sharedPref = getApplicationContext().getSharedPreferences("sharedPref_light", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SharedPreferences configPref = PreferenceManager.getDefaultSharedPreferences(this);
        clientUsername = configPref.getString("user_name", null);
        clientId = configPref.getString("UniqueUserID", null);



        msgToSend = findViewById(R.id.messageToSend);
        FloatingActionButton fab = findViewById(R.id.messageButton);
        fab.setImageResource(R.drawable.messagesend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgToSend.getText().toString();
                Log.d("msg", "msg sent: " + msg);
                if (msg.isEmpty() || msg.trim().length()==0){
                    msgToSend.setText("");
                } else {
                    // String msg= "pogger!";
                    publishMessage(msg + "," + clientId + "," + clientUsername + "," + Calendar.getInstance().getTime().getTime());

                    msgToSend.setText("");
                }
                //addToHistory("test");
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.history_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HistoryAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setContext(this);


        String clientId2 = String.valueOf(clientId) + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId2);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                // Show icon on top right?

                if (reconnect) {
                    addSYSTEMToHistory("Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    addSYSTEMToHistory("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addSYSTEMToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {

                Log.d("msgArrived", new String(message.getPayload()));
                String[] msg = new String(message.getPayload()).split(",");
                // msg[0] message
                // msg[1] clientID
                // msg[2] clientUsername
                // msg[3] timestamp (millisec)

                addMsgToHistory(msg[0],msg[1],msg[2],msg[3]);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
      });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(300000); // 5 min
//        mqttConnectOptions.setWill(publishTopic,("Client " + clientId + " disconnected!").getBytes(),1,true);
//
        addSYSTEMToHistory("Connecting to " + serverUri + "...");
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addSYSTEMToHistory("Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null)?
                            exception.toString() : exception.getCause()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            addSYSTEMToHistory(e.toString());
        }
    }

    private void addSYSTEMToHistory(String mainText) {
        //System.out.println("SYSTEM LOG: " + mainText);
        mAdapter.addSYSTEM(mainText,new SimpleDateFormat("HH:mm").format( new java.util.Date(System.currentTimeMillis())).toString());
        //Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void addMsgToHistory(String msg, String clientID, String clientUsername, String timestamp) {
        //System.out.println("LOG: " + msg);

        mAdapter.addMsg(msg, clientID, clientUsername,new SimpleDateFormat("HH:mm").format(new java.util.Date(Long.valueOf(timestamp).longValue())).toString());

        //Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(publishTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    addSYSTEMToHistory("Subscribed to: " + eventName);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addSYSTEMToHistory("Failed to subscribe");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            addSYSTEMToHistory(e.toString());
        }

    }


    public void publishMessage(String msg) {
        MqttMessage message = new MqttMessage();

        message.setPayload(msg.getBytes());
        // message.setPayload(publishMessage.getBytes());
        message.setRetained(true);
        message.setQos(0);

        try {
            mqttAndroidClient.publish(publishTopic, message);
            //addToHistory("Message Published");
        } catch (Exception e) {
            e.printStackTrace();
            addSYSTEMToHistory(e.toString());
        }
        if (!mqttAndroidClient.isConnected()) {
            addSYSTEMToHistory("Client not connected!");
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpStyle(){

        SharedPreferences configPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(configPref.contains("static_theme")){
            Boolean selected = configPref.getBoolean("static_theme", false);
            String value = configPref.getString("static_theme_selected", null);

            if(selected){
                sensorManager.unregisterListener(this, lightSensor);
                //static
                if(Objects.equals(value, "light")){
                    changeStyle(0);
                }else if(Objects.equals(value, "medium")){
                    changeStyle(1);
                }else if(Objects.equals(value, "dark")){
                    changeStyle(2);
                }
            }else{
                //dynamic
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                firstMeasure = true;
            }


        }else {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            firstMeasure = true;
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        int level = -1;
        if(sharedPref.contains(sharedPref_key)){
            level = sharedPref.getInt(sharedPref_key, -1);
        }
        if(type == Sensor.TYPE_LIGHT){
            float value = sensorEvent.values[0];
            Log.d("value", Float.toString(value));
            if(value < 5 && (level != 2 || firstMeasure)){
                changeStyle(2);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 2);
                editor.apply();
            }else if (value > 150 && (level != 0 || firstMeasure)){
                changeStyle(0);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 0);
                editor.apply();
            }else if (value < 150 && value > 5 && (level != 1 || firstMeasure)){
                changeStyle(1);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPref_key, 1);
                editor.apply();
            }
            firstMeasure = false;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    private void changeStyle(int style){

        LinearLayout layout = findViewById(R.id.MQTTLayout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mqttToolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        LinearLayout layout1 = findViewById(R.id.MsgLayout);
        TextView messageToSend = findViewById(R.id.messageToSend);
        FloatingActionButton messageButton = findViewById(R.id.messageButton);

        switch (style){
            case 0:{
                mAdapter.changeStyle(style);
                layout.setBackgroundResource(R.color.light_background);
                layout1.setBackgroundResource(R.color.light_background);
                myToolbar.setBackgroundResource(R.color.light_primary);
                messageToSend.setTextColor(ContextCompat.getColor(this, R.color.light_text));


                break;
            }
            case 1:{
                mAdapter.changeStyle(style);
                layout.setBackgroundResource(R.color.medium_background);
                layout1.setBackgroundResource(R.color.medium_background);
                myToolbar.setBackgroundResource(R.color.medium_primary);
                messageToSend.setTextColor(ContextCompat.getColor(this, R.color.medium_text));



                break;

            }
            case 2:{
                mAdapter.changeStyle(style);
                layout.setBackgroundResource(R.color.dark_background);
                layout1.setBackgroundResource(R.color.dark_background);
                myToolbar.setBackgroundResource(R.color.dark_primary);
                messageToSend.setTextColor(ContextCompat.getColor(this, R.color.dark_text));


                break;

            }
        }
    }


    protected void onStart() {
        super.onStart();

        setUpStyle();
    }

}