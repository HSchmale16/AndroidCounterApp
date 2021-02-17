package org.henryschmale.counter.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.core.app.JobIntentService;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.EventSource;
import org.henryschmale.counter.models.EventTypeDetail;

import java.time.OffsetDateTime;
import java.util.MissingResourceException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * Allows pushing details about the
 */
public class CountEventWidgetIntentService extends JobIntentService {
    public static final String TAG = "EventWidgetService";
    public static final int JOB_ID = 10001;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_COUNT_EVENT_TYPE = "org.henryschmale.counter.widgets.action.CountEventType";

    // TODO: Rename parameters
    public static final String EXTRA_EVENT_TYPE_ID = "org.henryschmale.counter.widgets.extra.PARAM_EVENT_TYPE_ID";
    public static final String EXTRA_DIRECTION = "org.henryschmale.counter.widgets.extra.INCR_DIRECTION";
    public static final String EXTRA_SOURCE = "org.henryschmale.widgets.extra.SOURCE";

    public CountEventWidgetIntentService() {
        super();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionIncrCount(Context context, String eventTypeId, String direction, EventSource source) {
        Intent intent = new Intent(context, CountEventWidgetIntentService.class);
        intent.setAction(ACTION_COUNT_EVENT_TYPE);
        intent.putExtra(EXTRA_EVENT_TYPE_ID, eventTypeId);
        intent.putExtra(EXTRA_DIRECTION, direction);
        intent.putExtra(EXTRA_SOURCE, source.getCode());

        enqueueWork(context, CountEventWidgetIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        if (intent != null) {
            Log.d(TAG, intent.getAction());
            final String action = intent.getAction();
            if (ACTION_COUNT_EVENT_TYPE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_EVENT_TYPE_ID);
                final String param2 = intent.getStringExtra(EXTRA_DIRECTION);
                final int sourceCode = intent.getIntExtra(EXTRA_SOURCE, EventSource.UNKNOWN.getCode());
                final EventSource source = EventSource.getSourceFromCode(sourceCode);

                handleActionCountIncr(param1, param2, source);
            }
        }
    }

    /**
     * handles the action count incr. Requires strings for some reason. Mostly because I don't
     * fully understand how intents work yet.
     */
    private void handleActionCountIncr(String eventTypeIdStr, String direction, EventSource source) {
        assert eventTypeIdStr != null;
        assert direction != null;

        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());

        int eventTypeId = Integer.parseInt(eventTypeIdStr);

        EventTypeDetail detail = db.countedEventTypeDao().getEventDetailByIdNOW(eventTypeId);
        if (detail == null) {
            throw new RuntimeException("This is a missing type");
        }

        Log.i(TAG, "Service doing " + direction + " " + eventTypeIdStr + " " + detail.eventTypeName);

        CountedEvent event = new CountedEvent();

        event.countedEventTypeId = eventTypeId;
        event.source = source;

        if (direction.equals("UP")) {
            event.increment = 1;
        } else if (direction.equals("DOWN")) {
            event.increment = -1;
        } else {
            throw new UnsupportedOperationException("Must either UP or DOWN");
        }

        event.createdAt = OffsetDateTime.now();

        db.countedEventTypeDao().addCountedEvent(event);

        // Send broadcast to the new app widget.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), IncrDecrTypeWidget.class));
        Intent intent = new Intent(this, IncrDecrTypeWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        sendBroadcast(intent);
    }

}