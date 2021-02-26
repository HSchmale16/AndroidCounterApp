package org.henryschmale.counter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.henryschmale.counter.R;
import org.henryschmale.counter.widgets.CountEventWidgetIntentService;

import java.io.File;

public class ExportActivity extends AppCompatActivity {
    public static final String TAG = "ExportActivity";
    public static final String PROGRESS_NOTIFICATION_ACTION = "EXPORT_PROGRESS_NOTIFICATION";
    ProgressBar progressBar;
    TextView progressView;
    Button startExport;
    Button shareButton;
    String shareFileName;

    /**
     * We submit a background task to our job intent service to handle the export. We'll update
     * this activity using a broadcast receiver.
     */
    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final int progress = intent.getIntExtra("progress", 0);
            final int total = intent.getIntExtra("total", 100);
            shareFileName = intent.getStringExtra("exportFileName");

            //Log.d(TAG, "received broadcast event " + progress + " of " + total);

            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(total);
            progressBar.setProgress(progress, true);

            String text = getResources().getString(R.string.progress_text, progress, total, shareFileName);
            progressView.setText(text);

            if (progress >= total) {
                startExport.setEnabled(true);
                shareButton.setEnabled(true);
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
            shareButton.setEnabled(false);
            Log.d(TAG, "button pressed export starting");
            CountEventWidgetIntentService.startActionExport(getApplicationContext(), PROGRESS_NOTIFICATION_ACTION);
        });

        shareButton = findViewById(R.id.btn_share_via);
        shareButton.setOnClickListener(view -> {
            shareIt();
            // Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show();
        });
    }

    public void shareIt() {
        File imagePath = new File(getApplicationContext().getFilesDir(), "exports");
        File newFile = new File(imagePath, shareFileName);

        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "org.henryschmale.counter.fileprovider", newFile);

        Log.d(TAG, "tag " + fileUri);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
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
