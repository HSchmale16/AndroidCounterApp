package org.henryschmale.counter.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class EventTypeDetailsWithWidget {
    @Embedded
    public EventTypeDetail eventDetails;

    @Relation(
        parentColumn = "uid",
        entityColumn = "eventTypeId"
    )
    public List<CountedWidgetIdToEventType> widgetIds;
}
