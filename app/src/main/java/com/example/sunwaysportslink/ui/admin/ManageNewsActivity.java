package com.example.sunwaysportslink.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.SportsNews;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageNewsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNews;
    private ManageNewsAdapter manageNewsAdapter;
    private ArrayList<SportsNews> newsList = new ArrayList<>();

    private FirebaseService firebaseService;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, ManageNewsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_news);

        firebaseService = FirebaseService.getInstance();

        // Initialize views
        recyclerViewNews = findViewById(R.id.recycler_view_news);
//        Button btnAddNews = findViewById(R.id.btnAddNews);

        // Set up RecyclerView
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));
        manageNewsAdapter = new ManageNewsAdapter(newsList);
        recyclerViewNews.setAdapter(manageNewsAdapter);

        // Fetch and display the news from Firebase
        fetchNewsFromFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Manage News");       // Set the title of the page
        }

        AppCompatButton btnAddNews = findViewById(R.id.btn_add_news);

        // Handle Add News button click
        btnAddNews.setOnClickListener(v -> {
            // Start activity to add news
            CreateNewsActivity.startIntent(ManageNewsActivity.this

            );
        });
    }

    private void fetchNewsFromFirebase() {
        firebaseService.getReference("sports_news").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsList.clear();
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    SportsNews news = newsSnapshot.getValue(SportsNews.class);
                    if (news != null) {
                        news.setId(newsSnapshot.getKey()); // Store the key as ID
                        newsList.add(news);
                    }
                }
                manageNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageNewsActivity.this, "Failed to fetch news", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous screen (LoginActivity)
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
