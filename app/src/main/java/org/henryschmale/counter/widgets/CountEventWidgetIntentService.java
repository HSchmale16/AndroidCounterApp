package org.henryschmale.counter.widgets;

import android.Manifest;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.stream.JsonWriter;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventSource;
import org.henryschmale.counter.models.EventTypeDetail;
import org.henryschmale.counter.models.dao.ExportDao;
import org.henryschmale.counter.utils.DateConverter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.OffsetDateTime;
import java.util.List;
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

    public static final String EXTRA_EVENT_TYPE_ID = "org.henryschmale.counter.widgets.extra.PARAM_EVENT_TYPE_ID";
    public static final String EXTRA_DIRECTION = "org.henryschmale.counter.widgets.extra.INCR_DIRECTION";
    public static final String EXTRA_SOURCE = "org.henryschmale.counter.widgets.extra.SOURCE";
    public static final String EXTRA_OFFSET_DATE_TIME = "org.henryschmale.counter.widgets.extra.OFFSET_DATE_TIME";
    public static final String EXTRA_TRIGGER_WIDGET_UPDATE = "org.henryschmale.counter.widgets.extra.TRIGGER_WIDGET_UPDATE";

    public static final String ACTION_EXPORT_TO_FILE = "org.henryschmale.counter.widgets.action.ExportToFile";
    public static final String EXTRA_NOTIFY_ACTION_NAME = "org.henryschmale.counter.widgets.extra.NOTIFY_ACTION_NAME";

    /**
     * Some instance variables for processing while things happen
     */
    private int totalEvents;
    private int totalProcessed;
    private String progressNotifyEventName;


    /**
     *
     */
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

    public static void startActionExport(Context context, String notifyAction) {
        Intent intent = new Intent(context, CountEventWidgetIntentService.class);
        intent.setAction(ACTION_EXPORT_TO_FILE);
        intent.putExtra(EXTRA_NOTIFY_ACTION_NAME, notifyAction);

        enqueueWork(context, CountEventWidgetIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, intent.getAction());
        final String action = intent.getAction();
        if (ACTION_COUNT_EVENT_TYPE.equals(action)) {
            final String eventTypeId = intent.getStringExtra(EXTRA_EVENT_TYPE_ID);
            final String direction = intent.getStringExtra(EXTRA_DIRECTION);

            final int sourceCode = intent.getIntExtra(EXTRA_SOURCE, EventSource.UNKNOWN.getCode());
            final EventSource source = EventSource.getSourceFromCode(sourceCode);

            final String offsetDateTime = intent.getStringExtra(EXTRA_OFFSET_DATE_TIME);
            final OffsetDateTime dt = DateConverter.toOffsetDateTime(offsetDateTime);

            handleActionCountIncr(eventTypeId, direction, source, dt);
        } else if (ACTION_EXPORT_TO_FILE.equals(action)) {
            final String progressNotify = intent.getStringExtra(EXTRA_NOTIFY_ACTION_NAME);

            try {
                handleActionExport(progressNotify);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionExport(String progressNotify) throws Exception {
        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());
        ExportDao dao = db.exportDao();

        totalEvents = dao.totalEvents();
        progressNotifyEventName = progressNotify;
        totalProcessed = 0;

        Context context = getApplicationContext();
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("export.json", Context.MODE_PRIVATE))) {
            writeJson(dao, outputStreamWriter);
        }
    }

    private void writeJson(ExportDao dao, OutputStreamWriter writer) {
        try (JsonWriter json = new JsonWriter(writer)) {
            json.setIndent(" ");
            Log.d(TAG, "writing output");

            json.beginArray();
            for (List<CountedEventType> typeEvents = dao.getEventTypesPaged2(0);
                 typeEvents.size() > 0;
                 typeEvents = dao.getEventTypesPaged2(typeEvents.get(typeEvents.size() - 1).uid)
            ) {
                for (CountedEventType type : typeEvents) {
                    processSingleEventType(dao, type, json);
                }
            }
            json.endArray();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void processSingleEventType(ExportDao dao, CountedEventType type, JsonWriter json) throws IOException {
        json.beginObject();

        json.name("eventTypeId").value(type.uid);
        json.name("eventTypeName").value(type.eventTypeName);
        json.name("eventTypeDescription").value(type.eventTypeDescription);
        json.name("createdAt").value(type.createdAt.toString());

        // The votes are an array
        json.name("eventTypeVotes");
        json.beginArray();

        for (List<CountedEvent> events = dao.getAssociatedEvents(type.uid, 0);
             events.size() > 0;
             events = dao.getAssociatedEvents(type.uid, events.get(events.size() - 1).uid)
        ) {
            for (CountedEvent event : events) {
                json.beginObject();

                json.name("voteId").value(event.uid);
                json.name("increment").value(event.increment);
                json.name("when").value(event.createdAt.toString());
                json.name("lat").value(event.latitude);
                json.name("lon").value(event.longitude);
                json.name("accuracy").value(event.accuracy);
                json.name("source").value(event.source.toString());

                json.endObject();

                ++totalProcessed;
            }

            sendProgressBroadcast(progressNotifyEventName, totalProcessed, totalEvents);
        }

        json.endArray();

        json.endObject();
    }

    private void sendProgressBroadcast (String notifyAction, int progress, int totalProgress) {
        Intent intent = new Intent (notifyAction); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("progress", progress);
        intent.putExtra("total", totalProgress);
        Log.i(TAG, "total = " + progress + " / " + totalProgress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * handles the action count incr. Requires strings for some reason. Mostly because I don't
     * fully understand how intents work yet.
     *
     * TODO: record a video on why it needs location permission. This requires me to implement a better detail view
     * and maybe geocoding.
     */
    private void handleActionCountIncr(String eventTypeIdStr, String direction, EventSource source, OffsetDateTime timestamp) {
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

        event.createdAt = timestamp == null ? OffsetDateTime.now() : timestamp;

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            event.latitude = location.getLatitude();
            event.longitude = location.getLongitude();
            event.altitude = location.getAltitude();
            event.accuracy = location.getAccuracy();
            // Log.d(TAG, location.toString());
        }

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