package org.henryschmale.counter.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.CountedWidgetIdToEventType;
import org.henryschmale.counter.widgets.IncrDecrTypeWidget;

import java.util.ArrayList;
import java.util.List;

import static org.henryschmale.counter.activities.CreateEventTypeActivity.CREATE_EVENT_REQUEST_CODE;

/**
 * Configuration activity for picking event to track when setting up a widget.
 */
public class SetTrackedEventTypeActivity extends AppCompatActivity {
    public static final String TAG = "SetTrackedEventTypeActivity";
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    CountedEventDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tracked_event_type);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        } else {
            finish();
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        getSupportActionBar().setTitle("Select Event Type For Widget To Track");

        this.database = CountedEventDatabase.getInstance(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.event_type_list);
        EventTypeSelectListAdapter adapter = new EventTypeSelectListAdapter();

        database.countedEventTypeDao().getEventTypesOrderByNameAsc().observe(this, adapter::setData);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent fabIntent = new Intent(SetTrackedEventTypeActivity.this, CreateEventTypeActivity.class);
            startActivityForResult(fabIntent, CREATE_EVENT_REQUEST_CODE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE && data != null) {
            //eventTypeAdapter.notifyItemChanged(data.getIntExtra("newItemId", 50));
        }
    }

    class EventTypeSelectListAdapter extends RecyclerView.Adapter<EventTypeSelectListAdapter.ViewHolder> {
        List<CountedEventType> data = new ArrayList<>();

        public void setData(List<CountedEventType> theData) {
            this.data = theData;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pick_event_type_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.updateFrom(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            int eventTypeId;
            TextView text;
            Button button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.event_type_name);
                button = itemView.findViewById(R.id.submit_button);
                button.setOnClickListener(this);
            }

            void updateFrom(CountedEventType event) {
                text.setText(event.eventTypeName);
                eventTypeId = event.uid;
            }

            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Log.d(TAG, "Clicked button for " + eventTypeId);

                //SharedPreferences sharedPreferences = context.getSharedPreferences("counter_widget_settings", Context.MODE_PRIVATE);
                //sharedPreferences.edit().putInt("widget" + appWidgetId, eventTypeId).apply();
                AsyncTask.execute(() -> {
                    database.widgetDao().insertAppWidget(new CountedWidgetIdToEventType(appWidgetId, eventTypeId));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                    IncrDecrTypeWidget.updateAppWidget(context, appWidgetManager, appWidgetId, eventTypeId);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                });
            }
        }
    }
}