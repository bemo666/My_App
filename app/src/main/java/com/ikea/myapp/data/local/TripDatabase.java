package com.ikea.myapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ikea.myapp.models.MyTrip;

@Database(entities =  {MyTrip.class}, version = 15, exportSchema = false)
@TypeConverters({DateConverter.class, ByteConverter.class, PlanHeaderConverter.class, BudgetConverter.class,
        CustomCurrencyConverter.class, CustomDateTimeConverter.class})
public abstract class TripDatabase extends RoomDatabase {

    public abstract TripDao tripDao();
    private static TripDatabase instance;

    public static TripDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (TripDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(), TripDatabase.class, "trip_database")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return  instance;
    }

}
