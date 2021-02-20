package org.henryschmale.counter;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.room.testing.MigrationTestHelper;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CountedEventDatabaseTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public CountedEventDatabaseTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                CountedEventDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrateAll() throws IOException {
        // Create earliest version of the database.
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 7);
        db.close();

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        CountedEventDatabase appDb = Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                CountedEventDatabase.class, TEST_DB)
                .addMigrations(ALL_MIGRATIONS).build();
        appDb.getOpenHelper().getWritableDatabase();
        appDb.close();
    }

    // Array of all migrations
    private static final Migration[] ALL_MIGRATIONS = new Migration[]{
            CountedEventDatabase.MIGRATION_7_8,
            CountedEventDatabase.MIGRATION_8_9,
            CountedEventDatabase.MIGRATION_9_10,
            CountedEventDatabase.MIGRATION_10_11,
            CountedEventDatabase.MIGRATION_11_12,
            CountedEventDatabase.MIGRATION_12_13
    };
}