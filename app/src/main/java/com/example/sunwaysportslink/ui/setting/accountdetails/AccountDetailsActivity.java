package com.example.sunwaysportslink.ui.setting.accountdetails;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailsActivity extends AppCompatActivity {

    private AppCompatButton saveButton;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText genderEditText;
    private FirebaseService firebaseService;
    private String userId;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private CircleImageView profileImage;
    private ImageView cameraIcon, ivEdit;
    private Spinner spinnerFavouriteEvent;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, AccountDetailsActivity.class);
        context.startActivity(intent); // Start the RegisterActivity directly
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        firebaseService = FirebaseService.getInstance();
        userId = firebaseService.getAuth().getCurrentUser().getUid();

        saveButton = findViewById(R.id.btn_save);
        usernameEditText = findViewById(R.id.et_username);
        emailEditText = findViewById(R.id.et_email);
        phoneEditText = findViewById(R.id.et_phone);
        genderEditText = findViewById(R.id.et_gender);
        spinnerFavouriteEvent = findViewById(R.id.spinner_favourite_sports);
        profileImage = findViewById(R.id.profileImage);
        cameraIcon = findViewById(R.id.iv_camera);
        ivEdit = findViewById(R.id.iv_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpSpinner();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back arrow
            getSupportActionBar().setTitle("Edit Profile");       // Set the title of the page
        }

        loadUserDetails();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here you would save the changes to the user details
                saveUserData();
            }
        });

        // Set click listener on the edit icon
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                profileImage.setImageURI(selectedImage); // Set selected image to profileImage
                uploadImageToFirebase(selectedImage); // Upload the selected image
            } else if (requestCode == TAKE_PHOTO && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileImage.setImageBitmap(imageBitmap); // Set captured image to profileImage

                // Convert the Bitmap to Uri and upload to Firebase Storage
                Uri imageUri = getImageUriFromBitmap(this, imageBitmap);
                uploadImageToFirebase(imageUri); // Upload the captured image
            }
        }
    }

    // Helper function to convert Bitmap to Uri
    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "ProfilePic", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Convert the Uri to a string before storing it in Firebase
        String imageUrl = imageUri.toString();

        // Update the user profile image URL under the "users" node
        firebaseService.getUserRef(userId).child("profileImageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AccountDetailsActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AccountDetailsActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserData() {
        // Collect data from EditTexts
        String updatedUsername = usernameEditText.getText().toString();
        String updatedEmail = emailEditText.getText().toString();
        String updatedPhone = phoneEditText.getText().toString();
        String updatedGender = genderEditText.getText().toString();
        String updatedFavouriteSports = spinnerFavouriteEvent.getSelectedItem().toString();

        // Create a HashMap to store the updated user details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", updatedUsername);
        userDetails.put("email", updatedEmail);
        userDetails.put("phone", updatedPhone);
        userDetails.put("gender", updatedGender);
        userDetails.put("favourite_sports", updatedFavouriteSports);

        // Save data to Firebase Realtime Database
        firebaseService.getUserRef(userId).updateChildren(userDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Disable EditTexts after saving
                usernameEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                genderEditText.setEnabled(false);
                spinnerFavouriteEvent.setEnabled(false);

                // Hide the Save Button
                saveButton.setVisibility(View.GONE);
                cameraIcon.setVisibility(View.GONE);

                // Notify the user of success
                Toast.makeText(AccountDetailsActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the error
                Toast.makeText(AccountDetailsActivity.this, "Failed to update details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSpinner() {
        String[] eventTypes = {"Basketball", "Football", "Futsal", "Volleyball", "Tennis"};
        spinnerFavouriteEvent.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes));
    }

    private void loadUserDetails() {
        firebaseService.getUserRef(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate the EditText fields with user data
                    usernameEditText.setText(dataSnapshot.child("username").getValue(String.class));
                    emailEditText.setText(dataSnapshot.child("email").getValue(String.class));
                    phoneEditText.setText(dataSnapshot.child("phone").getValue(String.class));
                    genderEditText.setText(dataSnapshot.child("gender").getValue(String.class));
                    spinnerFavouriteEvent.setSelection(getIndexForSpinner(spinnerFavouriteEvent, dataSnapshot.child("favourite_sports").getValue(String.class)));  // Assuming 'title' refers to event type


                    // Load and display the profile image (if it exists)
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    if (!isDestroyed()) {
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            // Use Glide to load the image into the profileImage view
                            Glide.with(AccountDetailsActivity.this).load(profileImageUrl).placeholder(R.drawable.iv_default_profile)  // Placeholder image while loading
                                    .error(R.drawable.iv_default_profile)        // Fallback if image fails to load
                                    .into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.iv_default_profile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private int getIndexForSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;  // Default to first item if not found
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

    private void toggleEditMode() {
        usernameEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        phoneEditText.setEnabled(true);
        genderEditText.setEnabled(true);
        spinnerFavouriteEvent.setEnabled(true);

        // Make the Save Button visible
        saveButton.setVisibility(View.VISIBLE);
        cameraIcon.setVisibility(View.VISIBLE);
    }
}