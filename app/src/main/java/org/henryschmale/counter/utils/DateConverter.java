package org.henryschmale.counter.utils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateConverter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String value) {
        if (value == null) {
            return OffsetDateTime.now();
        }
        return formatter.parse(value, OffsetDateTime::from);
    }

    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime date) {
        if (date == null) {
            return OffsetDateTime.now().format(formatter);
        }
        return date.format(formatter);
    }
}