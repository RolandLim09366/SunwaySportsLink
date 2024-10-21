package com.example.sunwaysportslink.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.SportsNews;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final ArrayList<SportsNews> sportsNewsList;

    public NewsAdapter(ArrayList<SportsNews> sportsNewsList) {
        this.sportsNewsList = sportsNewsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        SportsNews newsItem = sportsNewsList.get(position);
        holder.tvNewsTitle.setText(newsItem.getTitle());
        holder.tvNewsDescription.setText(newsItem.getDescription());

        // Load the image using Glide
        if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(newsItem.getImageUrl())
                    .placeholder(R.drawable.ic_camera) // optional placeholder
                    .into(holder.ivNewsImage);
        } else {
            holder.ivNewsImage.setImageResource(R.drawable.ic_camera); // default placeholder
        }

        // Set click listener to navigate to the news details page
        holder.itemView.setOnClickListener(v -> {
            // Navigate to NewsDetailActivity (to be created)
            Intent intent = new Intent(holder.itemView.getContext(), NewsDetailActivity.class);
            intent.putExtra("newsTitle", newsItem.getTitle());
            intent.putExtra("newsDescription", newsItem.getDescription());
            intent.putExtra("newsImageUrl", newsItem.getImageUrl());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sportsNewsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsTitle, tvNewsDescription;
        ImageView ivNewsImage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsDescription = itemView.findViewById(R.id.tvNewsDescription);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
        }
    }
}

