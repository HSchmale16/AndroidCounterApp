package org.henryschmale.counter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.henryschmale.counter.CountedEventTypeListAdapter;
import org.henryschmale.counter.R;
import org.henryschmale.counter.widgets.CountEventWidgetIntentService;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExportActivity extends AppCompatActivity {
    public static final String TAG = "ExportActivity";
    public static final String PROGRESS_NOTIFICATION_ACTION = "EXPORT_PROGRESS_NOTIFICATION";
    public static final String ACTION_TRIGGER_EXPORT_LIST_UPDATE = "TRIGGER_EXPORT_LIST_UPDATE";
    public static final String ACTION_SHARE_FILE = "SHARE_FILE";
    public static final String EXTRA_SHARE_FILE_NAME = "EXTRA_FILE_NAME";

    ProgressBar progressBar;
    TextView progressView;
    Button startExport;
    Button shareButton;
    String shareFileName;
    PreviousExportsRecyclerAdapter adapter;

    /**
     * We submit a background task to our job intent service to handle the export. We'll update
     * this activity using a broadcast receiver.
     */
    private final BroadcastReceiver  bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ACTION_TRIGGER_EXPORT_LIST_UPDATE)) {
                adapter.updateFileList();
                RecyclerView r = findViewById(R.id.recyclerView);
                r.scrollToPosition(adapter.getItemCount() - 1);
            } else if (action.equals(PROGRESS_NOTIFICATION_ACTION)) {
                final int progress = intent.getIntExtra("progress", 0);
                final int total = intent.getIntExtra("total", 100);
                shareFileName = intent.getStringExtra("exportFileName");

                //Log.d(TAG, "received broadcast event " + progress + " of " + total);

                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(total);
                progressBar.setProgress(progress);

                String text = getResources().getString(R.string.progress_text, progress, total, shareFileName);
                progressView.setText(text);

                if (progress >= total) {
                    startExport.setEnabled(true);
                    shareButton.setEnabled(true);
                }
            } else if (action.equals(ACTION_SHARE_FILE)) {
                final String file2Share = intent.getStringExtra(EXTRA_SHARE_FILE_NAME);
                shareIt(file2Share, getApplicationContext());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        getSupportActionBar().setTitle(R.string.export_your_data);

        progressBar = findViewById(R.id.the_progress_bar);
        progressView = findViewById(R.id.progress_text);

        startExport = findViewById(R.id.start_export);
        startExport.setOnClickListener(view -> {
            progressBar.setIndeterminate(true);
            startExport.setEnabled(false);
            shareButton.setEnabled(false);
            Log.d(TAG, "button pressed export starting");
            CountEventWidgetIntentService.startActionExport(getApplicationContext(), PROGRESS_NOTIFICATION_ACTION);
        });

        shareButton = findViewById(R.id.btn_share_via);
        shareButton.setOnClickListener(view -> {
            shareIt(shareFileName, getApplicationContext());
            // Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show();
        });

        adapter = new PreviousExportsRecyclerAdapter(getApplicationContext());
        RecyclerView view = findViewById(R.id.recyclerView);
        view.setAdapter(adapter);
    }

    public void shareIt(String shareFileName, @NonNull Context context) {
        File imagePath = new File(context.getFilesDir(), "exports");
        File newFile = new File(imagePath, shareFileName);

        Uri fileUri = FileProvider.getUriForFile(context, "org.henryschmale.counter.fileprovider", newFile);

        Log.d(TAG, "tag " + fileUri);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.send_to)));
    }

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(PROGRESS_NOTIFICATION_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(ACTION_TRIGGER_EXPORT_LIST_UPDATE));
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(ACTION_SHARE_FILE));

    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }


    private static class PreviousExportsRecyclerAdapter extends RecyclerView.Adapter<PreviousExportsRecyclerAdapter.ViewHolder> {
        List<String> fileList = new ArrayList<>();
        Context context;

        public PreviousExportsRecyclerAdapter(Context context) {
            this.context = context;
            updateFileList();
        }

        public void updateFileList() {
            File imagePath = new File(context.getFilesDir(), "exports");
            fileList = Arrays.asList(imagePath.list());
            fileList.sort(String::compareTo);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.export_file_list_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.update(fileList.get(position));
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView fileName;
            TextView sizeView;
            ImageButton shareButton;
            ImageButton deleteButton;
            Context context;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.fileName = itemView.findViewById(R.id.file_name_text);
                this.shareButton = itemView.findViewById(R.id.btn_export_me);
                this.shareButton.setOnClickListener(this);
                this.deleteButton = itemView.findViewById(R.id.btn_delete_export_forever);
                this.deleteButton.setOnClickListener(this);
                this.context = itemView.getContext();
                this.sizeView = itemView.findViewById(R.id.export_file_size);
            }

            public void update(String filename) {
                this.fileName.setText(filename);

                File imagePath = new File(context.getFilesDir(), "exports");
                File newFile = new File(imagePath, filename);
                this.sizeView.setText(Long.toString(newFile.length()));
            }

            @Override
            public void onClick(View view) {
                if (view.equals(shareButton)) {
                    Intent intent = new Intent(ACTION_SHARE_FILE);
                    intent.putExtra(EXTRA_SHARE_FILE_NAME, this.fileName.getText().toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else if (view.equals(deleteButton)) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    File imagePath = new File(context.getFilesDir(), "exports");
                                    File newFile = new File(imagePath, fileName.getText().toString());
                                    newFile.delete();
                                    Intent intent = new Intent(ExportActivity.ACTION_TRIGGER_EXPORT_LIST_UPDATE);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    Toast.makeText(context, "Export will NOT be deleted", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setMessage(R.string.dialog_delete_event_type)
                            .setTitle(R.string.title_delete_event_type)
                            .setPositiveButton(R.string.delete_affirmative, dialogClickListener)
                            .setNegativeButton(R.string.delete_negative, dialogClickListener)
                            .show();
                } else {
                    Log.d(TAG, "Received click that isn't mine");
                }
            }
        }
    }
}
