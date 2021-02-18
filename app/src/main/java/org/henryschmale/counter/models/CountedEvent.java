package org.henryschmale.counter.models;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.henryschmale.counter.utils.DateConverter;
import org.henryschmale.counter.utils.LocationTypeConverter;

import java.time.OffsetDateTime;

/**
 * A count instance
 */
@RequiresApi(api = Build.VERSION_CODES.O)
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
@TypeConverters({DateConverter.class, EventSource.class})
public class CountedEvent {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    public OffsetDateTime createdAt = OffsetDateTime.now();

    @ColumnInfo(index = true)
    public int countedEventTypeId;

    public byte increment;

    public EventSource source;

    public Double latitude;
    public Double longitude;
    public Double altitude;
    public Float accuracy;
}
