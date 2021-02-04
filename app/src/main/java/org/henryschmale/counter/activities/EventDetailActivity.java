package org.henryschmale.counter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.EventTypeDetail;

import java.util.concurrent.ExecutionException;

public class EventDetailActivity extends AppCompatActivity {
    public final static String EXTRA_EVENT_DETAIL_ID = "EVENT_DETAIL_ID";
    public final static String TAG = "EventDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().setTitle("TESTING");

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_DETAIL_ID, 1);

        Log.d(TAG, Integer.toString(eventId));

        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());

        LiveData<EventTypeDetail> eventListenableFuture = db.countedEventTypeDao().getEventDetailById(eventId);

        eventListenableFuture.observe(this, eventTypeDetail -> {
            getSupportActionBar().setTitle(eventTypeDetail.eventTypeName);
            ((TextView)findViewById(R.id.incr_count)).setText(Long.toString(eventTypeDetail.incrementCount));
            ((TextView)findViewById(R.id.decr_count)).setText(Long.toString(eventTypeDetail.decrementCount));
            ((TextView)findViewById(R.id.event_type_description)).setText(eventTypeDetail.description);

        });
    }
}