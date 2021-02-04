package org.henryschmale.counter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;

import java.time.OffsetDateTime;

/**
 * Contains a counted event or counter that we are counting up occurances of
 */
@Entity(indices = {
        @Index(value = {"event_type_name"}, unique = true)
        })
@TypeConverters(DateConverter.class)
public class CountedEventType {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "event_type_name")
    public String eventTypeName;

    @ColumnInfo(name = "event_type_description")
    public String eventTypeDescription;

    public OffsetDateTime createdAt = OffsetDateTime.now();
}
