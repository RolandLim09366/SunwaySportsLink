package com.example.sunwaysportslink.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.SportsNews;

import java.util.ArrayList;

public class ManageNewsAdapter extends RecyclerView.Adapter<ManageNewsAdapter.NewsViewHolder> {

    private ArrayList<SportsNews> newsList;

    public ManageNewsAdapter(ArrayList<SportsNews> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        SportsNews news = newsList.get(position);

        // Bind data to the views
        holder.tvNewsNumber.setText(String.valueOf(position + 1));  // Display the item number
        holder.tvNewsTitle.setText(news.getTitle());
        holder.tvNewsDescription.setText(news.getDescription());
//        holder.tvPublishedDate.setText(news.getPublishedDate());

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            // Delete the news from Firebase
            FirebaseService.getInstance().getReference("sports_news").child(news.getId()).removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    // ViewHolder class for the RecyclerView
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvNewsNumber, tvNewsTitle, tvNewsDescription, tvPublishedDate;
        View btnDelete;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNewsNumber = itemView.findViewById(R.id.tv_news_number);
            tvNewsTitle = itemView.findViewById(R.id.tv_news_title);
            tvNewsDescription = itemView.findViewById(R.id.tv_news_description);
//            tvPublishedDate = itemView.findViewById(R.id.tv_released_date);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
