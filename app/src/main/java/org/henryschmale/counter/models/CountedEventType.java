package org.henryschmale.counter.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;

import java.time.OffsetDateTime;

/**
 * Contains a counted event or counter that we are counting up occurrences of
 */
@Entity(indices = {
        @Index(value = {"event_type_name"}, unique = true)
        })
@TypeConverters(DateConverter.class)
public class CountedEventType {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    @ColumnInfo(name = "event_type_name")
    public String eventTypeName;

    @ColumnInfo(name = "event_type_description")
    public String eventTypeDescription;

    public OffsetDateTime createdAt = OffsetDateTime.now();

    @ColumnInfo(name = "color", defaultValue = "0xFFFFFFFF")
    public int color;
}
