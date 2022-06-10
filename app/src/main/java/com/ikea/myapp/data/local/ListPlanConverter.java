package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikea.myapp.models.Plan;

import java.lang.reflect.Type;
import java.util.List;

public class ListPlanConverter {

    @TypeConverter
    public static  List<Plan> fromString(String value) {
        Type listType = new TypeToken<List<Plan>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<Plan> header) {
        Gson gson = new Gson();
        String json = gson.toJson(header);
        return json;
    }
}