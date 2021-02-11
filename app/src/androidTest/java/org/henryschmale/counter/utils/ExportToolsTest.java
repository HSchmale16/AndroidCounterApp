package org.henryschmale.counter.utils;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.CountedEventTypeDao;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeWithVotes;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExportToolsTest {
    CountedEventDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, CountedEventDatabase.class)
                .build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testEventTypeWithVotes() {
        CountedEventTypeDao dao = db.countedEventTypeDao();

        CountedEventType type = new CountedEventType();
        type.createdAt = OffsetDateTime.now();
        type.eventTypeName = "Foo";
        type.uid = (int)dao.addCountedEventType(type);

        for (int i = 0; i < 10; ++i) {
            CountedEvent event = new CountedEvent();
            event.countedEventTypeId = type.uid;
            event.increment = i % 2 == 0 ? (byte)1 : (byte)-1;
            event.createdAt = OffsetDateTime.now();

            dao.addCountedEvent(event);
        }

        List<EventTypeWithVotes> votes = dao.getVotesDetail();

        assertEquals(1, votes.size());
        assertEquals(type.uid, votes.get(0).eventType.uid);
        assertEquals(10, votes.get(0).countedEvents.size());

        type = new CountedEventType();
        type.createdAt = OffsetDateTime.now();
        type.eventTypeName = "Bar";
        type.uid = (int)dao.addCountedEventType(type);

        votes = dao.getVotesDetail();

        assertEquals(2, votes.size());
        assertEquals(10, votes.get(0).countedEvents.size());
        assertEquals(0, votes.get(1).countedEvents.size());
    }

    @Test
    public void testExportToolsForJson() {
        Assert.fail("Not yet implemented");
    }

}