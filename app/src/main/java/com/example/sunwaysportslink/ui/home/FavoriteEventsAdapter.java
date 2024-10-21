package com.example.sunwaysportslink.ui.home;

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

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.ViewHolder> {

    private final ArrayList<Event> favoriteEventsList;

    public FavoriteEventsAdapter(ArrayList<Event> favoriteEventsList) {
        this.favoriteEventsList = favoriteEventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = favoriteEventsList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return favoriteEventsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventTitle;
        private final TextView tvEventDate;
        private final TextView tvEventVenue;
        private final ImageView ivEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventVenue = itemView.findViewById(R.id.tvEventVenue);
            ivEvent = itemView.findViewById(R.id.imgEventBackground);
        }

        public void bind(Event event) {
            // Bind the event details to the views
            tvEventTitle.setText(event.getTitle());
            tvEventDate.setText(event.getDate());
            tvEventVenue.setText(event.getVenue());

            switch (event.getTitle().toLowerCase()) {
                case "basketball":
                    ivEvent.setImageResource(R.drawable.iv_basketball);
                    break;
                case "football":
                    ivEvent.setImageResource(R.drawable.iv_football);
                    break;
                case "tennis":
                    ivEvent.setImageResource(R.drawable.iv_tennis);
                    break;
                case "futsal":
                    ivEvent.setImageResource(R.drawable.iv_futsal);
                    break;
                case "volleyball":
                    ivEvent.setImageResource(R.drawable.iv_volleyball);
                    break;
                default:
                    ivEvent.setImageResource(R.drawable.iv_sports);  // Default image
                    break;
            }
        }
    }
}

