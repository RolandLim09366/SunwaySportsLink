// EventAdapter.java
package com.example.sunwaysportslink.ui.search;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.databinding.ItemEventBinding;
import com.example.sunwaysportslink.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final OnEventClickListener onEventClickListener;

    public EventAdapter(List<Event> eventList, OnEventClickListener onEventClickListener) {
        this.eventList = eventList;
        this.onEventClickListener = onEventClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemEventBinding binding = ItemEventBinding.inflate(inflater, parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> updatedEvents) {
        this.eventList = updatedEvents;
        notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        private final ItemEventBinding binding;

        public EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Event event) {
            binding.tvEventTitle.setText(event.getTitle());
            binding.tvEventDateTime.setText(event.getDate());
            binding.tvEventLocation.setText(event.getVenue());
            binding.tvOrganizer.setText("Host: " + event.getCreatedBy());

            switch (event.getTitle().toLowerCase()) {
                case "basketball":
                    binding.ivSports.setImageResource(R.drawable.iv_basketball);
                    break;
                case "football":
                    binding.ivSports.setImageResource(R.drawable.iv_football);
                    break;
                case "tennis":
                    binding.ivSports.setImageResource(R.drawable.iv_tennis);
                    break;
                case "futsal":
                    binding.ivSports.setImageResource(R.drawable.iv_futsal);
                    break;
                case "volleyball":
                    binding.ivSports.setImageResource(R.drawable.iv_volleyball);
                    break;
                default:
                    binding.ivSports.setImageResource(R.drawable.iv_sports);  // Default image
                    break;
            }

            // Set click listener for each event
            itemView.setOnClickListener(v -> onEventClickListener.onEventClick(event));
        }
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
