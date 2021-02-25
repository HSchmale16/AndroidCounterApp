package org.henryschmale.counter;

import android.content.Context;

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
import org.henryschmale.counter.models.CountedWidgetIdToEventType;
import org.henryschmale.counter.models.dao.ExportDao;
import org.henryschmale.counter.models.dao.WidgetDao;

import java.time.OffsetDateTime;

@Database(
        version = 13,
        entities = {
                CountedEventType.class,
                CountedEvent.class,
                CountedWidgetIdToEventType.class
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

    public abstract WidgetDao widgetDao();

    public abstract ExportDao exportDao();

    public static synchronized CountedEventDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, CountedEventDatabase.class, "database-name")
                    .addMigrations(
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_9_10,
                            MIGRATION_10_11,
                            MIGRATION_11_12,
                            MIGRATION_12_13
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(databaseSeeder)
                    //.allowMainThreadQueries()
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

    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE widget_id_event_type(" +
                            "`app_widget_id` INTEGER PRIMARY KEY NOT NULL, " +
                            "`eventTypeId` INTEGER NOT NULL, " +
                            "FOREIGN KEY (`eventTypeId`) REFERENCES CountedEventType(`uid`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_widget_id_event_type_eventTypeId ON widget_id_event_type(eventTypeId)");
        }
    };

    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // drop last location column
            database.execSQL("CREATE TABLE `CountedEvent_new` (" +
                    "`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`createdAt` TEXT, " +
                    "`countedEventTypeId` INTEGER NOT NULL, " +
                    "`increment` INTEGER NOT NULL," +
                    " `source` INTEGER,  " +
                    "FOREIGN KEY(`countedEventTypeId`) REFERENCES `CountedEventType`(`uid`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("INSERT INTO CountedEvent_new(uid, createdAt, countedEventTypeId, increment, source) SELECT uid, createdAt, countedEventTypeId, increment, source FROM CountedEvent");
            database.execSQL("DROP TABLE CountedEvent");
            database.execSQL("ALTER TABLE CountedEvent_new RENAME TO CountedEvent");


            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN latitude REAL");
            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN longitude REAL");
            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN altitude REAL");
            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN accuracy REAL");
        }
    };

    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN last_location VARCHAR(200)");
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

    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CountedEvent ADD COLUMN source INTEGER;");
        }
    };
}
