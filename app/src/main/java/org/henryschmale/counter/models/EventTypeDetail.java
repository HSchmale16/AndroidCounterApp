package org.henryschmale.counter.models;

import androidx.room.DatabaseView;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;

import java.time.OffsetDateTime;

/**
 * Database View For Event Types with Details
 */
@DatabaseView(
    "SELECT " +
            "event_type_name as eventTypeName, " +
            "CountedEventType.uid, " +
            "CountedEventType.event_type_description as description, " +
            "CountedEventType.createdAt as eventTypeCreated, " +
            "SUM(CountedEvent.increment) as netScore, " +
            "COUNT(*) as voteCount, " +
            "MAX(CountedEvent.createdAt) as lastUpdated, " +
            "SUM(CASE increment WHEN 1 THEN 1 ELSE 0 END) as incrementCount, " +
            "SUM(CASE increment WHEN -1 THEN 1 ELSE 0 END) as decrementCount" +
            "  FROM CountedEventType " +
            "LEFT JOIN CountedEvent ON CountedEvent.countedEventTypeId = CountedEventType.uid " +
            "GROUP BY CountedEventType.uid"
)
@TypeConverters(DateConverter.class)
public class EventTypeDetail {
    public int uid;

    public String eventTypeName;

    public String description;

    public OffsetDateTime eventTypeCreated;

    public OffsetDateTime lastUpdated;

    public long decrementCount;

    public long voteCount;

    public long incrementCount;

    public long netScore;
}
