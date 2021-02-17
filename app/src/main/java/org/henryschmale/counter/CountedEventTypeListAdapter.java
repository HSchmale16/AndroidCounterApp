package org.henryschmale.counter;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.henryschmale.counter.models.CountedEvent;
import org.henryschmale.counter.models.CountedEventType;
import org.henryschmale.counter.models.EventTypeDetail;
import org.henryschmale.counter.widgets.CountEventWidgetIntentService;

import java.util.List;

public class CountedEventTypeListAdapter extends RecyclerView.Adapter<CountedEventTypeListAdapter.ViewHolder> {
    public static final String TAG = "APP_CET_Adapter";
    CountedEventTypeDao dao;
    List<CountedEventType> eventTypeDetails;
    LiveData<List<CountedEventType>> countedEventType;
    LongClickRecyclerHandler owner;

    public enum SortOrder {
        BY_NAME_A_Z,
        BY_NAME_Z_A,
        OLDEST_CREATED,
        NEWEST_CREATED
    };

    SortOrder sortOrder;

    public CountedEventTypeListAdapter(CountedEventTypeDao dao, LongClickRecyclerHandler owner) {
        this.owner = owner;
        this.dao = dao;
        this.setSortOrder(SortOrder.BY_NAME_A_Z);
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        if (this.countedEventType != null) {
            this.countedEventType.removeObservers(owner);
        }

        switch(this.sortOrder) {
            case BY_NAME_A_Z:
                this.countedEventType = dao.getEventTypesOrderByNameAsc();
                break;
            case BY_NAME_Z_A:
                this.countedEventType = dao.getEventTypesOrderByNameDesc();
                break;
            case OLDEST_CREATED:
                this.countedEventType = dao.getEventTypesOrderByOldestCreatedAt();
                break;
            case NEWEST_CREATED:
                this.countedEventType = dao.getEventTypesOrderByNewestCreatedAt();
                break;
        }

        this.countedEventType.observe(owner, this::setEventTypeDetails);
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public void setEventTypeDetails(List<CountedEventType> countedEventTypes) {
        this.eventTypeDetails = countedEventTypes;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.counted_event_type_incr_button_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, Integer.toString(position));

        LiveData<EventTypeDetail> cetFuture = dao.getEventDetailById(eventTypeDetails.get(position).uid);

        holder.setLiveDataSource(cetFuture, position);
    }

    @Override
    public int getItemCount() {
        return eventTypeDetails != null ? eventTypeDetails.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final TextView eventNameText;
        private final TextView counterText;
        private final ImageButton incrButton;
        private final ImageButton decrButton;
        private LiveData<EventTypeDetail> currentEventType;
        private int currentEventUid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.eventNameText = itemView.findViewById(R.id.event_type_name);
            this.counterText = itemView.findViewById(R.id.counter_text);
            this.incrButton = itemView.findViewById(R.id.btn_increment);
            this.decrButton = itemView.findViewById(R.id.btn_decrement);

            this.incrButton.setOnClickListener(this);
            this.decrButton.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        public void setLiveDataSource(LiveData<EventTypeDetail> details, int position) {
            // remove old references so no conflicts
            if (currentEventType != null) {
                currentEventType.removeObservers(owner);
            }
            Log.d(TAG, "postion = " + position);
            // setup to run.
            currentEventType = details;
            currentEventType.observe(owner, new Observer<EventTypeDetail>() {
                @Override
                public void onChanged(EventTypeDetail eventTypeDetail) {
                    if (eventTypeDetail == null) {
                        currentEventType.removeObservers(owner);
                        return;
                    }
                    currentEventUid = eventTypeDetail.uid;
                    updateFrom(eventTypeDetail);
                }
            });
        }

        public void updateFrom(@NonNull EventTypeDetail cet) {
            this.eventNameText.setText(cet.eventTypeName);
            this.counterText.setText(Long.toString(cet.netScore));
        }

        @Override
        public void onClick(View view) {
            if (view == this.incrButton) {
                recordEvent("UP");
            } else if (view == this.decrButton) {
                recordEvent("DOWN");
            } else {
                Log.w(TAG, "Click recorded for something that is not mine");
                Log.w(TAG, view.toString());
            }
        }

        private void recordEvent(String direction) {
            CountEventWidgetIntentService.startActionIncrCount(
                    owner.getApplicationContext(),
                    Integer.toString(currentEventUid), direction);
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d(TAG, "Long click detected");
            owner.handleLongClick(currentEventUid);
            return true;
        }
    }
}
