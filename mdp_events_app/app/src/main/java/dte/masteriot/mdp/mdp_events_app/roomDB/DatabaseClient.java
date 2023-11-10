package dte.masteriot.mdp.mdp_events_app.roomDB;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    //Class to manage the DB instance in order to only have one object of the database
    private Context ctx;
    private static DatabaseClient mInstance;
    private AppDatabase appDatabase ; //the app database object
    private DatabaseClient (Context ctx) {
        this.ctx = ctx;
        //create database
        appDatabase = Room.databaseBuilder(ctx, AppDatabase.class, "AppDatabase").build();
    }
    public static synchronized DatabaseClient getInstance (Context ctx) {
        //Get this class instance to get later the database instance
        if (mInstance == null)
            mInstance = new DatabaseClient(ctx);
        return mInstance;
    }
    public AppDatabase getAppDatabase () {
        return appDatabase ;
    } //retunr the DB instance

}
