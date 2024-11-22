package com.example.sunwaysportslink.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.ui.search.EventDetailsActivity;

import java.util.List;

public class ManageEventsAdapter extends RecyclerView.Adapter<ManageEventsAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final OnDeleteClickListener deleteClickListener;
    private final Context context;

    public ManageEventsAdapter(List<Event> eventList, Context context, OnDeleteClickListener deleteClickListener) {
        this.eventList = eventList;
        this.context = context;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false); // Assuming item_center.xml is your layout
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event, position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventTypeTextView;
        private final TextView venueTextView;
        private final TextView eventNumTextView;
        private final TextView expiredTextView;
        private final androidx.appcompat.widget.AppCompatButton deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeTextView = itemView.findViewById(R.id.tv_event_type);
            venueTextView = itemView.findViewById(R.id.tv_venue);
            eventNumTextView = itemView.findViewById(R.id.tv_events_number);
            expiredTextView = itemView.findViewById(R.id.tv_event_expired); // New TextView for expiration
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Event event, int position) {
            eventTypeTextView.setText(event.getTitle()); // Assuming event type is the title
            venueTextView.setText(event.getVenue());
            eventNumTextView.setText(String.valueOf(position + 1));

            if (event.isExpired()) {
                expiredTextView.setText("Expired");
                expiredTextView.setTextColor(context.getResources().getColor(R.color.blood_red)); // Optional: Set red color for expired
            } else {
                expiredTextView.setText("Ongoing");
                expiredTextView.setTextColor(context.getResources().getColor(R.color.steelblue)); // Optional: Set green color for ongoing
            }

            // Delete button click handling
            deleteButton.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(event, position);
                }
            });

            // Item click handling for navigating to EventDetailsActivity
            itemView.setOnClickListener(v -> {
                EventDetailsActivity.startIntent(context, event, true);
            });
        }
    }

    // Interface for handling delete button click events
    public interface OnDeleteClickListener {
        void onDeleteClick(Event event, int position);
    }
}