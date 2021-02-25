package org.henryschmale.counter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.gson.stream.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.dao.ExportDao;
import org.henryschmale.counter.widgets.CountEventWidgetIntentService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class ExportActivity extends AppCompatActivity {
    public static final String TAG = "ExportActivity";
    public static final String PROGRESS_NOTIFICATION_ACTION = "EXPORT_PROGRESS_NOTIFICATION";
    ProgressBar progressBar;
    TextView progressView;
    Button startExport;

    /**
     * We submit a background task to our job intent service to handle the export. We'll update
     * this activity using a broadcast receiver.
     */
    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            final int progress = intent.getIntExtra("progress", 0);
            final int total = intent.getIntExtra("total", 100);

            //Log.d(TAG, "received broadcast event " + progress + " of " + total);

            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(total);
            progressBar.setProgress(progress, true);

            String text = getResources().getString(R.string.progress_text, progress, total);
            progressView.setText(text);

            if (progress >= total) {
                startExport.setEnabled(true);
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        getSupportActionBar().setTitle(R.string.export_your_data);

        progressBar = findViewById(R.id.the_progress_bar);
        progressView = findViewById(R.id.textView);

        startExport = findViewById(R.id.start_export);


        startExport.setOnClickListener(view -> {
            startExport.setEnabled(false);
            Log.d(TAG, "button pressed export starting");
            CountEventWidgetIntentService.startActionExport(getApplicationContext(), PROGRESS_NOTIFICATION_ACTION);
        });
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(PROGRESS_NOTIFICATION_ACTION));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }

}
