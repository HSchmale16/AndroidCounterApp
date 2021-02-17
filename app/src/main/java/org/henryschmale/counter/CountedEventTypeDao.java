package org.henryschmale.counter;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeDetail;
import org.henryschmale.counter.models.EventTypeWithVotes;
import org.henryschmale.counter.models.VoteEntryModel;

import java.util.List;

@Dao
public interface CountedEventTypeDao {
    @Insert
    long addCountedEventType(CountedEventType eventType);

    @Query("SELECT Count(*) FROM CountedEventType WHERE event_type_name = :name")
    ListenableFuture<Integer> countEventTypeName(String name);

    @Query("SELECT * FROM CountedEventType WHERE uid = :uid")
    LiveData<CountedEventType> getEventTypeById(int uid);

    @Query("SELECT * FROM CountedEventType WHERE uid = :id")
    ListenableFuture<CountedEventType> getEventById(int id);

    @Query("SELECT * FROM EventTypeDetail WHERE uid = :id")
    LiveData<EventTypeDetail> getEventDetailById(int id);

    @Query("SELECT * FROM EventTypeDetail WHERE uid = :id")
    EventTypeDetail getEventDetailByIdNOW(int id);


    // BEGIN ORDERING METHODS OF COUNTED EVENT TYPE
    @Query("SELECT * FROM CountedEventType ORDER BY CountedEventType.event_type_name ASC")
    LiveData<List<CountedEventType>> getEventTypesOrderByNameAsc();

    @Query("SELECT * FROM CountedEventType ORDER BY CountedEventType.event_type_name DESC")
    LiveData<List<CountedEventType>> getEventTypesOrderByNameDesc();

    @Query("SELECT * FROM CountedEventType ORDER BY datetime(CountedEventType.createdAt) ASC")
    LiveData<List<CountedEventType>> getEventTypesOrderByOldestCreatedAt();

    @Query("SELECT * FROM CountedEventType ORDER BY datetime(CountedEventType.createdAt) DESC")
    LiveData<List<CountedEventType>> getEventTypesOrderByNewestCreatedAt();


    // END COUNTED EVENT TYPE ORDERING

    @Query("SELECT COUNT(*) FROM CountedEventType")
    ListenableFuture<Integer> totalCount();

    @Query("SELECT * FROM CountedEvent WHERE CountedEvent.countedEventTypeId = :id ORDER BY datetime(createdAt) DESC")
    LiveData<List<CountedEvent>> getCountedEventsOfType(int id);

    @Insert
    void addCountedEvent(CountedEvent event);

    @Transaction
    @Query("SELECT * FROM CountedEventType")
    List<EventTypeWithVotes> getVotesDetail();

    @Delete()
    void deleteEventType(CountedEventType type);

    @Query("SELECT COUNT(*) FROM CountedEvent")
    int getTotalCountedVotes();

}
