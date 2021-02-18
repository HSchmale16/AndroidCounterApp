package org.henryschmale.counter.utils;

import android.location.Location;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class LocationTypeConverter {

    @TypeConverter
    public static String locationAsJSON(Location location) {
        if (location != null) {
            Gson gson = new Gson();
            return gson.toJson(location);
        }
        return null;
    }

    @TypeConverter
    public static Location fromJSON(String json) {
        if (json != null) {
            Gson gson = new Gson();
            return gson.fromJson(json, Location.class);
        }
        return null;
    }
}
