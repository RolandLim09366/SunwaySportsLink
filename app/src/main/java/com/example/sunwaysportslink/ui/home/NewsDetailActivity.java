package com.example.sunwaysportslink.ui.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sunwaysportslink.R;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView tvNewsTitle, tvNewsDescription;
    private ImageView ivNewsImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize views
        tvNewsTitle = findViewById(R.id.tvNewsTitle);
        tvNewsDescription = findViewById(R.id.tvNewsDescription);
        ivNewsImage = findViewById(R.id.ivNewsImage);

        // Get data from the intent
        String title = getIntent().getStringExtra("newsTitle");
        String description = getIntent().getStringExtra("newsDescription");
        String imageUrl = getIntent().getStringExtra("newsImageUrl");

        // Set the data to views
        tvNewsTitle.setText(title);
        tvNewsDescription.setText(description);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("News Detail");       // Set the title of the page
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_camera).into(ivNewsImage);
        } else {
            ivNewsImage.setImageResource(R.drawable.iv_sports); // Default placeholder if no image
        }
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
