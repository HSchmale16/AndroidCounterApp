package org.henryschmale.counter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;

import java.util.concurrent.ExecutionException;

public class EventDetailActivity extends AppCompatActivity {
    public final static String EXTRA_EVENT_DETAIL_ID = "EVENT_DETAIL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().setTitle("TESTING");

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_DETAIL_ID, 1);

        CountedEventDatabase db = CountedEventDatabase.getInstance(getApplicationContext());

    }
}