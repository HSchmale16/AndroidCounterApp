package org.henryschmale.counter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.EventDetailVoteAdapter;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeDetail;

import java.util.List;

public class EventDetailActivity extends AppCompatActivity {
    public final static String EXTRA_EVENT_DETAIL_ID = "EVENT_DETAIL_ID";
    public final static String TAG = "EventDetailActivity";
    int targetEventTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        targetEventTypeId = getIntent().getIntExtra(EXTRA_EVENT_DETAIL_ID, -1);

        Log.d(TAG, Integer.toString(targetEventTypeId));

        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());

        LiveData<EventTypeDetail> eventTypeDetailLiveData = db.countedEventTypeDao().getEventDetailById(targetEventTypeId);

        eventTypeDetailLiveData.observe(this, eventTypeDetail -> {
            if (eventTypeDetail != null) {
                getSupportActionBar().setTitle(eventTypeDetail.eventTypeName);

                ((TextView) findViewById(R.id.net_count)).setText(Long.toString(eventTypeDetail.netScore));
                ((TextView) findViewById(R.id.incr_count)).setText(Long.toString(eventTypeDetail.incrementCount));
                ((TextView) findViewById(R.id.decr_count)).setText(Long.toString(eventTypeDetail.decrementCount));
                ((TextView) findViewById(R.id.total_vote_count)).setText(Long.toString(eventTypeDetail.voteCount));
                ((TextView) findViewById(R.id.event_type_description)).setText(eventTypeDetail.description);

                LiveData<List<CountedEvent>> events = db.countedEventTypeDao().getCountedEventsOfType(eventTypeDetail.uid);

                ((RecyclerView) findViewById(R.id.vote_list)).setAdapter(new EventDetailVoteAdapter(events, this));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_event_type) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            deleteAttachedEventType();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            Toast.makeText(getApplicationContext(), "It's safe", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage(R.string.dialog_delete_event_type)
                    .setTitle(R.string.title_delete_event_type)
                    .setPositiveButton(R.string.delete_affirmative, dialogClickListener)
                    .setNegativeButton(R.string.delete_negative, dialogClickListener)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAttachedEventType() {
        Log.d(TAG, "Deleting item " + targetEventTypeId);

        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());
        LiveData<CountedEventType> eventType = db.countedEventTypeDao().getEventTypeById(targetEventTypeId);
        eventType.observe(this, new Observer<CountedEventType>() {
            @Override
            public void onChanged(CountedEventType countedEventType) {
                db.countedEventTypeDao().deleteEventType(countedEventType);
                eventType.removeObservers(EventDetailActivity.this);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}