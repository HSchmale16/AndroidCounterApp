package org.henryschmale.counter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;

import java.time.OffsetDateTime;
import java.util.Date;

/**
 * A count instance
 */
@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = CountedEventType.class,
                        parentColumns = {"uid"},
                        childColumns = {"countedEventTypeId"},
                        onDelete = ForeignKey.CASCADE
                )
        }
)
@TypeConverters(DateConverter.class)
public class CountedEvent {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    public OffsetDateTime createdAt = OffsetDateTime.now();

    @ColumnInfo(index = true)
    public int countedEventTypeId;

    public byte increment;
}
