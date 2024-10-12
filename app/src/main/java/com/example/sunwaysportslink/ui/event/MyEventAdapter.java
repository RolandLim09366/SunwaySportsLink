package com.example.sunwaysportslink.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.Event;

import java.util.ArrayList;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.EventViewHolder> {

    private final ArrayList<Event> events;
    private final OnEventClickListener onEventClickListener;

    public MyEventAdapter(ArrayList<Event> events, OnEventClickListener onEventClickListener) {
        this.events = events;
        this.onEventClickListener = onEventClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view, onEventClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventVenue.setText(event.getVenue());
        holder.tvOrganizer.setText("Organiser: " + event.getCreatedBy());

        // Dynamically set the image based on the event type
        switch (event.getTitle().toLowerCase()) {
            case "basketball":
                holder.ivSports.setImageResource(R.drawable.iv_basketball);
                break;
            case "football":
                holder.ivSports.setImageResource(R.drawable.iv_football);
                break;
            case "tennis":
                holder.ivSports.setImageResource(R.drawable.iv_tennis);
                break;
            case "futsal":
                holder.ivSports.setImageResource(R.drawable.iv_futsal);
                break;
            case "volleyball":
                holder.ivSports.setImageResource(R.drawable.iv_volleyball);
                break;
            default:
                holder.ivSports.setImageResource(R.drawable.iv_sports);  // Default image
                break;
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEventTitle, tvEventDate, tvEventVenue, tvOrganizer;
        ImageView ivSports;
        OnEventClickListener onEventClickListener;

        public EventViewHolder(@NonNull View itemView, OnEventClickListener onEventClickListener) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tv_event_title);
            tvEventDate = itemView.findViewById(R.id.tv_event_date_time);
            tvEventVenue = itemView.findViewById(R.id.tv_event_location);
            tvOrganizer = itemView.findViewById(R.id.tv_organizer);
            ivSports = itemView.findViewById(R.id.iv_sports);

            this.onEventClickListener = onEventClickListener;
            itemView.setOnClickListener(this);  // Attach the listener to the whole item view
        }

        @Override
        public void onClick(View v) {
            // Invoke the click event when the user clicks on the item
            onEventClickListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventClickListener {
        void onEventClick(int position);
    }
}

