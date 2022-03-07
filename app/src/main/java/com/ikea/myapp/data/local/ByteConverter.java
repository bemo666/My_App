package com.ikea.myapp.data.local;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ByteConverter {

    @TypeConverter
    public static byte[] fromString(String value) {
        Type listType = new TypeToken<byte[]>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(byte[] list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
