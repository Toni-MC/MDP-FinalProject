package dte.masteriot.mdp.mdp_events_app.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Favourites.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(1);
    public abstract FavouriteDao favouriteDao();
}