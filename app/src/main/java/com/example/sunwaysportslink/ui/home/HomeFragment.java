package com.example.sunwaysportslink.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.example.sunwaysportslink.model.SportsNews;
import com.example.sunwaysportslink.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private ViewPager2 viewPagerFavorites;
    private RecyclerView recyclerViewNews;
    private FavoriteEventsAdapter favoriteEventsAdapter;
    private NewsAdapter newsAdapter;
    //    private NewsAdapter newsAdapter;
    private final ArrayList<Event> favoriteEventsList = new ArrayList<>();
    private final ArrayList<SportsNews> sportsNewsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        tvWelcome = view.findViewById(R.id.tvWelcome);
        viewPagerFavorites = view.findViewById(R.id.viewPagerFavorites);
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);

        // Set up the welcome message
        setupWelcomeMessage();

        // Set up ViewPager for favorite events
        setupFavoriteEventsViewPager();

        // Set up RecyclerView for news
        setupNewsRecyclerView();

        return view;
    }

    private void setupWelcomeMessage() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        String userId = firebaseService.getAuth().getCurrentUser().getUid();

        firebaseService.getUserRef(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null && user.getUsername() != null) {
                    tvWelcome.setText("Welcome, " + user.getUsername());
                } else {
                    tvWelcome.setText("Welcome, " + user.getEmail());
                }
            }
        });
    }

    private void setupFavoriteEventsViewPager() {
        favoriteEventsAdapter = new FavoriteEventsAdapter(favoriteEventsList);
        viewPagerFavorites.setAdapter(favoriteEventsAdapter);

        // Fetch favorite events for the user
        fetchFavoriteEvents();
    }

    private void fetchFavoriteEvents() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        String userId = firebaseService.getAuth().getCurrentUser().getUid();

        // Get user's favorite sports
        firebaseService.getUserRef(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    String favoriteSports = user.getFavourite_Sports();
                    if (favoriteSports != null && !favoriteSports.isEmpty()) {
                        // Fetch events that match the user's favorite sports
                        fetchEventsByFavoriteSports(favoriteSports);
                    } else {
                        // Fetch random events since the user has no favorite sports
                        fetchRandomEvents();
                    }
                }
            }
        });
    }

    private void fetchEventsByFavoriteSports(String favoriteSports) {
        FirebaseService firebaseService = FirebaseService.getInstance();
        firebaseService.getEventsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteEventsList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    if (favoriteEventsList.size() >= 5) break; // Limit to 5 events
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null && favoriteSports.equals(event.getTitle())) {
                        favoriteEventsList.add(event);
                    }
                }
                if (favoriteEventsList.isEmpty()) {
                    fetchRandomEvents();
                } else {
                    favoriteEventsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchRandomEvents() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        firebaseService.getEventsRef().limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteEventsList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        favoriteEventsList.add(event);
                    }
                }
                favoriteEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void setupNewsRecyclerView() {
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(sportsNewsList);
        recyclerViewNews.setAdapter(newsAdapter);

        // Fetch sports news (this can be hardcoded or fetched from an external API)
        fetchSportsNews();
    }

    private void fetchSportsNews() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        firebaseService.getReference("sports_news").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sportsNewsList.clear(); // Clear the previous data
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    String title = newsSnapshot.child("title").getValue(String.class);
                    String description = newsSnapshot.child("description").getValue(String.class);
                    String imageUrl = newsSnapshot.child("imageUrl").getValue(String.class);

                    if (title != null) {
                        // Create a model class for news if needed
                        SportsNews newsItem = new SportsNews(title, description, imageUrl);
                        sportsNewsList.add(newsItem);
                    }
                }
                newsAdapter.notifyDataSetChanged(); // Notify adapter about the new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any error while fetching data
            }
        });
    }

}
