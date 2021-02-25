package org.henryschmale.counter.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedWidgetIdToEventType;
import org.henryschmale.counter.models.EventSource;
import org.henryschmale.counter.models.EventTypeDetail;
import org.henryschmale.counter.models.EventTypeDetailsWithWidget;
import org.henryschmale.counter.models.dao.WidgetDao;

import java.util.List;

import static org.henryschmale.counter.widgets.CountEventWidgetIntentService.ACTION_COUNT_EVENT_TYPE;
import static org.henryschmale.counter.widgets.CountEventWidgetIntentService.EXTRA_DIRECTION;
import static org.henryschmale.counter.widgets.CountEventWidgetIntentService.EXTRA_EVENT_TYPE_ID;
import static org.henryschmale.counter.widgets.CountEventWidgetIntentService.EXTRA_SOURCE;

/**
 * Implementation of App Widget functionality.
 */
public class IncrDecrTypeWidget extends AppWidgetProvider {
    public static final String TAG = "IncrDecrTypeWidget";
    public static final String EXTRA_APP_WIDGET_ID = "org.henryschmale.counter.widgets.extra.APP_WIDGET_ID";
    public static final String EXTRA_CURRENT_COUNT = "org.henryschmale.counter.widgets.extra.CURRENT_COUNT";

    /**
     * We have to proxy button presses using a broadcast to ensure our app receives them
     * to pass on to the application
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_COUNT_EVENT_TYPE)) {
            Log.d(TAG, "received count event inc");
            intent.setClass(context, CountEventWidgetIntentService.class);
            String eventTypeId = intent.getStringExtra(EXTRA_EVENT_TYPE_ID);
            String direction = intent.getStringExtra(EXTRA_DIRECTION);

            CountEventWidgetIntentService.startActionIncrCount(context, eventTypeId, direction, EventSource.WIDGET);
        }
    }


    private static PendingIntent getIntentForIncr(Context context, String direction, int eventTypeId, long currentCount) {
        String id = Integer.toString(eventTypeId);

        Log.d(TAG, "Creating " + direction + " intent for event id = " + eventTypeId);

        Intent incrIntent = new Intent(context, IncrDecrTypeWidget.class);
        incrIntent.setAction(ACTION_COUNT_EVENT_TYPE);
        incrIntent.putExtra(EXTRA_EVENT_TYPE_ID, Integer.toString(eventTypeId));
        incrIntent.putExtra(EXTRA_DIRECTION, direction);
        incrIntent.putExtra(EXTRA_CURRENT_COUNT, currentCount);

        int requestCode = (direction.equals("UP") ? 1 : -1) * eventTypeId;

        return PendingIntent.getBroadcast(context, requestCode, incrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int eventTypeId) {
        // Construct the RemoteViews object
        CountedEventDatabase db = CountedEventDatabase.getInstance(context);

        RemoteViews views = getRemoteViews(context, 0, "");

        EventTypeDetail x = db.countedEventTypeDao().getEventDetailByIdNOW(eventTypeId);

        Log.d(TAG, "update request for appwidget = " + appWidgetId + " eventId = " + eventTypeId);

        if (eventTypeId != -1 && x != null) {
            views.setTextViewText(R.id.event_type_name, x.eventTypeName);
            views.setTextViewText(R.id.event_type_count, Long.toString(x.netScore));

            PendingIntent incrIntent = getIntentForIncr(context, "UP", eventTypeId, x.netScore);
            views.setOnClickPendingIntent(R.id.btn_increment, incrIntent);

            PendingIntent decrIntent = getIntentForIncr(context, "DOWN", eventTypeId, x.netScore);
            views.setOnClickPendingIntent(R.id.btn_decrement, decrIntent);
        } else {
            views.setTextViewText(R.id.event_type_name, context.getString(R.string.widget_big_time_error));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // We need the database and we don't want to run on main thread so use async task
        AsyncTask.execute(
                () -> {
                    CountedEventDatabase db = CountedEventDatabase.getInstance(context);
                    WidgetDao dao = db.widgetDao();
                    List<CountedWidgetIdToEventType> widgets = dao.getEventsForAppIds(appWidgetIds);

                    for (CountedWidgetIdToEventType widget : widgets) {
                        updateAppWidget(context, appWidgetManager, widget.appWidgetId, widget.eventTypeId);
                    }

                }
        );
    }

    private static RemoteViews getRemoteViews(Context context, long count, String eventName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_incr_decr_type);

        views.setTextViewText(R.id.event_type_name, eventName);
        views.setTextViewText(R.id.event_type_count, Long.toString(count));

        return views;
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * We are storing persistent data about the widgets. Set up the remap when they are restored
     * @param context
     * @param oldWidgetIds
     * @param newWidgetIds
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        CountedEventDatabase db = CountedEventDatabase.getInstance(context);

        db.widgetDao().updateAppWidgetIds(oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        CountedEventDatabase db = CountedEventDatabase.getInstance(context);

        db.widgetDao().deleteAppWidgets(appWidgetIds);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}