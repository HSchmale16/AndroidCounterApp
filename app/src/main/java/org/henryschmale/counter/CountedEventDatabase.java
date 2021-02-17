package org.henryschmale.counter;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeDetail;
import org.henryschmale.counter.models.VoteEntryModel;

import java.time.OffsetDateTime;

@Database(
        version = 9,
        entities = {
                CountedEventType.class,
                CountedEvent.class
        },
        views = {
                EventTypeDetail.class,
                VoteEntryModel.class
        },
        exportSchema = true
)
public abstract class CountedEventDatabase extends RoomDatabase {
    private static CountedEventDatabase INSTANCE = null;

    public abstract CountedEventTypeDao countedEventTypeDao();

    public static CountedEventDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                    CountedEventDatabase.class, "database-name")
                    .addMigrations(
                            MIGRATION_7_8,
                            MIGRATION_8_9
                    )
                    .addCallback(databaseSeeder)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    static final RoomDatabase.Callback databaseSeeder = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            db.execSQL("INSERT INTO CountedEventType(event_type_name, event_type_description, createdAt) VALUES (?, ?, ?)",
                    new Object[]{"Example event type", "This is an example event type. Use the increment buttons on the main screen to count up things related to it", OffsetDateTime.now().toString()});

            db.execSQL("INSERT INTO CountedEventType(event_type_name, event_type_description, createdAt) VALUES (?, ?, ?)",
                    new Object[]{"Another example type", "This is an example event type. Use the increment buttons on the main screen to count up things related to it. Long press to delete me", OffsetDateTime.now().toString()});

            db.execSQL("INSERT INTO CountedEventType(event_type_name, event_type_description, createdAt) VALUES (?, ?, ?)",
                    new Object[]{"Zeta Event", "This is an example event type. Use the increment buttons on the main screen to count up things related to it. Long press to delete me", OffsetDateTime.now().toString()});

        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // it needs to be in this format in order to use a hex digit for it here and for room to not complain
            //noinspection SyntaxError
            database.execSQL("ALTER TABLE CountedEventType ADD COLUMN color INTEGER NOT NULL DEFAULT 0xFFFFFFFF");
            database.execSQL("DROP VIEW IF EXISTS VoteEntryModel");
            database.execSQL("CREATE VIEW `VoteEntryModel` AS SELECT " +
                    "CountedEventType.uid " +
                    ", CountedEventType.event_type_name as eventTypeName " +
                    ", CountedEventType.createdAt as typeCreatedAt" +
                    ", CountedEvent.createdAt as votedOn" +
                    ", CountedEvent.increment" +
                    "  FROM CountedEventType " +
                    "INNER JOIN CountedEvent ON CountedEvent.countedEventTypeId = CountedEventType.uid ");
        }
    };

    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("INSERT INTO CountedEventType(event_type_name, event_type_description, createdAt) VALUES (?, ?, ?)",
                new Object[]{"Example event type", "This is an example event type. Use the increment buttons on the main screen to count up things related to it", OffsetDateTime.now().toString()});
        }
    };
}
