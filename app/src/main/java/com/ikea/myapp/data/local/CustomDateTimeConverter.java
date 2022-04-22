package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ikea.myapp.models.CustomDateTime;

import java.lang.reflect.Type;

public class CustomDateTimeConverter {

    @TypeConverter
    public static CustomDateTime fromString(String value) {
        Type listType = new TypeToken<CustomDateTime>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(CustomDateTime customDateTime) {
        Gson gson = new Gson();
        String  json = gson.toJson(customDateTime);
        return json;
    }
}
