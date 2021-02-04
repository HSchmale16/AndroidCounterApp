package org.henryschmale.counter;

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
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CountedEventTypeListAdapter extends RecyclerView.Adapter<CountedEventTypeListAdapter.ViewHolder> {
    public static final String TAG = "APP_CET_Adapter";
    CountedEventTypeDao dao;
    LifecycleOwner owner;

    public CountedEventTypeListAdapter(CountedEventTypeDao dao, LifecycleOwner owner) {
        this.dao = dao;
        this.owner = owner;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.counted_event_type_incr_button_view, parent, false);
        return new ViewHolder(view, dao);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, Integer.toString(position));

        LiveData<EventTypeDetail> cetFuture = dao.getEventDetailById(position + 1);

        holder.setLiveDataSource(cetFuture, position);
    }

    @Override
    public int getItemCount() {
        try {
            ListenableFuture<Integer> count = dao.totalCount();
            return count.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView eventNameText;
        private final TextView counterText;
        private final Button incrButton;
        private final Button decrButton;
        private final CountedEventTypeDao dao;
        private LiveData<EventTypeDetail> currentEventType;
        private int currentEventUid;

        public ViewHolder(@NonNull View itemView, CountedEventTypeDao dao) {
            super(itemView);

            this.eventNameText = itemView.findViewById(R.id.event_type_name);
            this.counterText = itemView.findViewById(R.id.counter_text);
            this.incrButton = itemView.findViewById(R.id.btn_increment);
            this.decrButton = itemView.findViewById(R.id.btn_decrement);
            this.dao = dao;

            this.incrButton.setOnClickListener(this);
            this.decrButton.setOnClickListener(this);
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
                recordEvent((byte)1);
            } else if (view == this.decrButton) {
                recordEvent((byte) -1);
            } else {
                Log.w(TAG, "Click recorded for something that is not mine");
                Log.w(TAG, view.toString());
            }
        }

        private void recordEvent(byte direction) {
            Log.d(TAG, Byte.toString(direction));
            CountedEvent event = new CountedEvent();
            event.countedEventTypeId = currentEventUid;
            event.increment = direction;

            this.dao.addCountedEvent(event);
        }
    }
}
