package org.henryschmale.counter;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        version = 6,
        entities = {
                CountedEventType.class,
                CountedEvent.class
        },
        views = {
                EventTypeDetail.class
        },
        exportSchema = false
)
public abstract class CountedEventDatabase extends RoomDatabase {
        private static CountedEventDatabase INSTANCE = null;

        public abstract CountedEventTypeDao countedEventTypeDao();

        public static CountedEventDatabase getInstance(final Context context) {
                if (INSTANCE == null) {
                        CountedEventDatabase db = Room.databaseBuilder(context,
                                CountedEventDatabase.class, "database-name")
                                .allowMainThreadQueries()
                                .fallbackToDestructiveMigration()
                                .build();
                        INSTANCE = db;
                }
                return INSTANCE;
        }
}
