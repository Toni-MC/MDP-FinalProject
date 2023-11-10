package dte.masteriot.mdp.mdp_events_app.roomDB;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Query("SELECT * FROM favourites")
    List<Favourites> getAllFavourites();

    @Query("SELECT * FROM favourites WHERE eventID = (:eventID)")
    List<Favourites> getFavouriteByEventID(Long eventID);

    @Query("DELETE FROM favourites WHERE eventID = (:eventID)")
    void deleteFavourite(Long eventID);

    @Insert
    void insertAll(Favourites... favourites);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Favourites favourites);


}
