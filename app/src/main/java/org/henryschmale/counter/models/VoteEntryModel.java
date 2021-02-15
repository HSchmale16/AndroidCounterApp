package org.henryschmale.counter.models;

import androidx.room.DatabaseView;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;

import java.time.OffsetDateTime;

@DatabaseView(
        "SELECT " +
                "CountedEventType.uid " +
                ", CountedEventType.event_type_name as eventTypeName " +
                ", CountedEventType.createdAt as typeCreatedAt" +
                ", CountedEvent.createdAt as votedOn" +
                ", CountedEvent.increment" +
                "  FROM CountedEventType " +
                "INNER JOIN CountedEvent ON CountedEvent.countedEventTypeId = CountedEventType.uid "
)
@TypeConverters(DateConverter.class)
public class VoteEntryModel {
    public int uid;

    public String eventTypeName;

    public OffsetDateTime typeCreatedAt;

    public OffsetDateTime votedOn;

    public byte increment;
}
