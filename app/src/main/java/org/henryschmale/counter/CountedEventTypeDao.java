package org.henryschmale.counter;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeDetail;

import java.util.List;

@Dao
public interface CountedEventTypeDao {
    @Insert
    long addCountedEventType(CountedEventType eventType);

    @Query("SELECT Count(*) FROM CountedEventType WHERE event_type_name = :name")
    ListenableFuture<Integer> countEventTypeName(String name);

    @Query("SELECT * FROM CountedEventType WHERE uid = :id")
    ListenableFuture<CountedEventType> getEventById(int id);

    @Query("SELECT * FROM EventTypeDetail WHERE uid = :id")
    LiveData<EventTypeDetail> getEventDetailById(int id);

    @Query("SELECT COUNT(*) FROM CountedEventType")
    ListenableFuture<Integer> totalCount();

    @Query("SELECT * FROM CountedEvent WHERE CountedEvent.countedEventTypeId = :id ORDER BY datetime(createdAt) DESC")
    LiveData<List<CountedEvent>> getCountedEventsOfType(int id);

    @Insert
    void addCountedEvent(CountedEvent event);
}
