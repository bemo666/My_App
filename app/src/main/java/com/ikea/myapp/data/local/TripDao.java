package com.ikea.myapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ikea.myapp.MyTrip;

import java.util.List;

@Dao
public interface TripDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MyTrip trip);

    @Query("SELECT * from trips ORDER BY startStamp ASC")
    LiveData<List<MyTrip>> getTrips();

    @Update
    void updateTrip(MyTrip trip);

    @Query("DELETE from trips")
    void deleteTable();

    @Delete
    void deleteTrip(MyTrip trip);

//    @Query("Update trips Set image = :image Where id = :id")
//    void setImage(String id, byte[] image);


}
