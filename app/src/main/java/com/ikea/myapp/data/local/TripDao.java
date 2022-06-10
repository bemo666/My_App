package com.ikea.myapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ikea.myapp.models.MyTrip;

import java.util.List;

@Dao
public interface TripDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MyTrip trip);

    @Query("SELECT * from trips ORDER BY startStamp ASC")
    LiveData<List<MyTrip>> getTrips();

    @Query("SELECT * from trips WHERE id LIKE :newId")
    LiveData<MyTrip> getTrip(String newId);

    @Query("SELECT image from trips WHERE id == :newId")
    LiveData<String> getTripImage(String newId);

    @Update
    void updateTrip(MyTrip trip);

    @Query("DELETE from trips")
    void deleteTable();

    @Query("UPDATE trips SET image = :image WHERE id = :id")
    void setTripImage(String id, String image);

    @Delete
    void deleteTrip(MyTrip trip);

    @Query("UPDATE trips SET imageVersion = CASE WHEN  imageVersion < 9 THEN (imageVersion + 1)  ELSE 1 END WHERE id = :id ")
    void incrementImageVersion(String id);

}
