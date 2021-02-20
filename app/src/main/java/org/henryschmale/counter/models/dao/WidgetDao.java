package org.henryschmale.counter.models.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.models.CountedWidgetIdToEventType;
import org.henryschmale.counter.models.EventTypeDetailsWithWidget;

import java.util.List;

@Dao
public abstract class WidgetDao {
    @Transaction
    @Query("SELECT * FROM EventTypeDetail WHERE uid = :uid")
    public abstract EventTypeDetailsWithWidget getWidgetsByEventTypeId(int uid);

    @Query("SELECT * FROM widget_id_event_type WHERE app_widget_id IN (:appWidgets)")
    public abstract List<CountedWidgetIdToEventType> getEventsForAppIds(int[] appWidgets);

    @Transaction
    @Query("UPDATE widget_id_event_type SET app_widget_id = :newId WHERE app_widget_id = :oldId")
    public abstract void updateAppWidgetIds(int oldId, int newId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAppWidget(CountedWidgetIdToEventType newWidget);

    @Query("DELETE FROM widget_id_event_type WHERE app_widget_id in (:widgetId)")
    public abstract void deleteAppWidgets(int[] widgetId);

    @Transaction
    public void updateAppWidgetIds(int[] oldWidgetIds, int[] newWidgetIds) {
        for (int i = 0; i < oldWidgetIds.length; ++i) {
            this.updateAppWidgetIds(oldWidgetIds[i], newWidgetIds[i]);
        }
    }
}
