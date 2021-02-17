package org.henryschmale.counter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.widgets.IncrDecrTypeWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration activity for picking event to track when setting up a widget.
 */
public class SetTrackedEventTypeActivity extends AppCompatActivity {
    public static final String TAG = "SetTrackedEventTypeActivity";
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tracked_event_type);

        getSupportActionBar().setTitle("Select Event Type For Widget To Track");

        CountedEventDatabase database = CountedEventDatabase.getInstance(getApplicationContext());

        RecyclerView view = findViewById(R.id.event_type_list);
        EventTypeSelectListAdapter adapter = new EventTypeSelectListAdapter();

        database.countedEventTypeDao().getEventTypesOrderByNameAsc().observe(this, adapter::setData);
        view.setAdapter(adapter);

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

                SharedPreferences sharedPreferences = context.getSharedPreferences("counter_widget_settings", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("widget" + appWidgetId, eventTypeId).apply();


                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                IncrDecrTypeWidget.updateAppWidget(context, appWidgetManager, appWidgetId, eventTypeId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        }
    }
}