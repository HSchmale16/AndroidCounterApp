package org.henryschmale.counter.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "widget_id_event_type",
        foreignKeys = {
                @ForeignKey(
                        entity = CountedEventType.class,
                        parentColumns = "uid",
                        childColumns = "eventTypeId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class CountedWidgetIdToEventType {
    @PrimaryKey
    @ColumnInfo(name = "app_widget_id")
    public int appWidgetId;

    @ColumnInfo(index = true)
    public int eventTypeId;

    public CountedWidgetIdToEventType() {
    }

    @Ignore
    public CountedWidgetIdToEventType(int appWidgetId, int eventTypeId) {
        this.appWidgetId = appWidgetId;
        this.eventTypeId = eventTypeId;
    }
}
