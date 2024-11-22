package com.example.sunwaysportslink.ui.admin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.SportsNews;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNewsActivity extends AppCompatActivity {

    private EditText etNewsTitle;
    private EditText etNewsDescription;
    private DatabaseReference sportsNewsRef;
    private ImageView ivNews;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Uri selectedImageUri; // Store the selected image URI


    public static void startIntent(Context context) {
        Intent intent = new Intent(context, CreateNewsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Create News");     // Set the title of the page
        }

        FirebaseService firebaseService = FirebaseService.getInstance();

        // Initialize Firebase Database reference for SportsNews
        sportsNewsRef = firebaseService.getInstance().getReference("sports_news");

        // Initialize EditTexts
        etNewsTitle = findViewById(R.id.et_news_title);
        etNewsDescription = findViewById(R.id.et_news_description);
        ivNews = findViewById(R.id.iv_news);

        // Set up the button click listener
        Button btnCreateNews = findViewById(R.id.btn_create_news);
        btnCreateNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSportsNews();
            }
        });

        ivNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });
    }

    private void saveSportsNews() {
        String title = etNewsTitle.getText().toString().trim();
        String description = etNewsDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadImage(selectedImageUri, title, description);  // Upload the image and then save news details
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri, String title, String description) {
        // Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://sunwaysportslink.firebasestorage.app");
        StorageReference storageRef = storage.getReference().child("news_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Save the news item with the download URL
                    saveNewsToDatabase(title, description, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(CreateNewsActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveNewsToDatabase(String title, String description, String imageUrl) {
        String newsId = sportsNewsRef.push().getKey(); // Get a unique ID for the news
        if (newsId == null) return;

        SportsNews sportsNews = new SportsNews(title, description, imageUrl);
        sportsNews.setId(newsId); // Set the news ID

        // Save the SportsNews object to Firebase Realtime Database
        sportsNewsRef.child(newsId).setValue(sportsNews).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateNewsActivity.this, "News created successfully", Toast.LENGTH_SHORT).show();
                resetFields();
                finish();
            } else {
                Toast.makeText(CreateNewsActivity.this, "Failed to create news", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetFields() {
        etNewsTitle.setText("");
        etNewsDescription.setText("");
        ivNews.setImageResource(R.drawable.iv_sports); // Reset to a placeholder image
        selectedImageUri = null; // Reset the selected image URI
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Navigate back to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action");
        String[] options = {"Take a photo", "Select from gallery"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Take a photo
                    checkCameraPermission();
                } else if (which == 1) {
                    // Select from gallery
                    selectImageFromGallery();
                }
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                selectedImageUri = data.getData();
                ivNews.setImageURI(selectedImageUri); // Show selected image in the ImageView
            } else if (requestCode == TAKE_PHOTO && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                selectedImageUri = getImageUriFromBitmap(this, imageBitmap); // Convert to Uri
                ivNews.setImageBitmap(imageBitmap);
            }
        }
    }

    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "news", null);
        return Uri.parse(path);
    }
}
