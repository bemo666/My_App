package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikea.myapp.Budget;

import java.lang.reflect.Type;

public class BudgetConverter {

    @TypeConverter
    public static Budget fromString(String value) {
        Type listType = new TypeToken<Budget>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(Budget budget) {
        Gson gson = new Gson();
        String json = gson.toJson(budget);
        return json;
    }
}
