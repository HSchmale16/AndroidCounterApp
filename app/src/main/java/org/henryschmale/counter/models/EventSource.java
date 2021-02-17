package org.henryschmale.counter.models;

import androidx.room.TypeConverter;

public enum EventSource {
    WIDGET(1),
    EVENT_LIST(2),
    NOTIFICATION(3),
    UNKNOWN(4);

    private final Integer code;

    EventSource(int code) {
        this.code = code;
    }

    public Integer getCode(){
        return code;
    }

    @TypeConverter
    public static EventSource getSourceFromCode(Integer code) {
         for (EventSource es : values()) {
             if (es.code.equals(code)) {
                 return es;
             }
         }
        return null;
    }

    @TypeConverter
    public static Integer getCodeFromSource(EventSource source) {
         if (source != null) {
             return source.code;
         }

         return null;
    }

}
