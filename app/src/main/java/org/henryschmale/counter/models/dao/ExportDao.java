package org.henryschmale.counter.models.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeWithVotes;

import java.util.List;


/**
 * A dao of our own just for exporting the events.
 */
@Dao
public interface ExportDao {
    /**
     * Implement my own crappy paging
     * @param eventTypeId
     * @param lastUid
     * @return
     */
    @Query("SELECT * FROM CountedEvent WHERE CountedEvent.countedEventTypeId = :eventTypeId AND CountedEvent.uid > :lastUid ORDER BY CountedEvent.uid ASC LIMIT 10")
    List<CountedEvent> getAssociatedEvents(int eventTypeId, long lastUid);

    @Query("SELECT * FROM CountedEventType WHERE uid > :lastUid ORDER BY uid ASC LIMIT 4")
    List<CountedEventType> getEventTypesPaged2(int lastUid);

    @Query("SELECT COUNT(*) FROM CountedEvent")
    int totalEvents();
}
