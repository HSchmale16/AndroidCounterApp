package org.henryschmale.counter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.henryschmale.counter.models.CountedEvent;

import java.util.List;

public class EventDetailVoteAdapter extends RecyclerView.Adapter<EventDetailVoteAdapter.EventDetailViewHolder> {
    public static final String TAG = "EventDetailVoteAdapter";
    List<CountedEvent> events;
    Activity activity;


    public EventDetailVoteAdapter(LiveData<List<CountedEvent>> events, Activity activity) {
        this.activity = activity;
        events.observe((LifecycleOwner) activity, new Observer<List<CountedEvent>>() {
            @Override
            public void onChanged(List<CountedEvent> countedEvents) {
                setEvents(countedEvents);
            }
        });
    }

    void setEvents(List<CountedEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_detail_vote, parent, false);
        return new EventDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventDetailViewHolder holder, int position) {
        holder.setup(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    public class EventDetailViewHolder extends RecyclerView.ViewHolder {
        Button indicator;
        TextView when;

        public EventDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
            when = itemView.findViewById(R.id.when_set);
        }

        void setup(CountedEvent event) {
            Log.d(TAG, event.toString());

            if (event.increment > 0) {
                indicator.setBackgroundColor(activity.getResources().getColor(R.color.green));
            } else {
                indicator.setBackgroundColor(activity.getResources().getColor(R.color.red));
            }

            this.when.setText(event.createdAt.toString());
        }
    }
}
