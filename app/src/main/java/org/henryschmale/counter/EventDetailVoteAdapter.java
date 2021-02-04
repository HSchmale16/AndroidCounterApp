package org.henryschmale.counter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventDetailVoteAdapter extends RecyclerView.Adapter<EventDetailVoteAdapter.EventDetailViewHolder> {

    @NonNull
    @Override
    public EventDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull EventDetailViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class EventDetailViewHolder extends RecyclerView.ViewHolder {

        public EventDetailViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
