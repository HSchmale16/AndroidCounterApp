package org.henryschmale.counter.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class EventTypeWithVotes {
    @Embedded
    public CountedEventType eventType;

    @Relation(
            parentColumn = "uid",
            entityColumn = "countedEventTypeId"
    )
    public List<CountedEvent> countedEvents;
}
