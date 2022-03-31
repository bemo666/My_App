package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikea.myapp.models.PlanHeader;

import java.lang.reflect.Type;
import java.util.List;

public class PlanHeaderConverter {

    @TypeConverter
    public static  List<PlanHeader> fromString(String value) {
        Type listType = new TypeToken<List<PlanHeader>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<PlanHeader> header) {
        Gson gson = new Gson();
        String json = gson.toJson(header);
        return json;
    }
}
