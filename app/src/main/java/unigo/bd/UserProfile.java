package unigo.bd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private EditText usernameEditText, emailEditText;
    private CircleImageView imageView;
    private ImageView back;
    private Button saveButton;
    private Uri imagePath;
    private ProgressBar progressBar;
    private static final int IMAGE_REQ = 1;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private String profileImageUrl; // Holds uploaded image URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Views
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        imageView = findViewById(R.id.profile_image);
        saveButton = findViewById(R.id.saveButton);
        back = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar_userProfile);

        back.setOnClickListener(v -> finish());

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userRef = FirebaseDatabase.getInstance().getReference("userInfo").child(currentUser.getUid());

        // Initialize Cloudinary
        initCloudinary();

        // Fetch User Info
        fetchUserInfo();

        // Select Image
        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        IMAGE_REQ);
            }
        });

        // Save Button Click
        saveButton.setOnClickListener(v -> {
            if (imagePath != null) {
                uploadImageToCloudinary(); // Upload image and then save user data
            } else {
                saveUserData(); // Only save username if no new image selected
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserInfo(); // Fetch user info when returning to this activity
    }

    private void initCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dhxb2civn");
        config.put("api_key", "791541517185888");
        config.put("api_secret", "GFV1357EDbPw9tFoCHFtdMWKHQY");
        MediaManager.init(this, config);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            imageView.setImageURI(imagePath); // Temporarily display selected image
        }
    }

    private void uploadImageToCloudinary() {
        if (imagePath == null) {
            saveUserData(); // No new image, just update user details
            return;
        }

        MediaManager.get().upload(imagePath)
                .unsigned("SANAHID") // Use unsigned upload preset
                .option("resource_type", "image")
                .option("folder", "UserProfiles")
                .constrain(TimeWindow.immediate())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        profileImageUrl = (String) resultData.get("secure_url");
                        saveUserData(); // Save user data after uploading image
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Toast.makeText(UserProfile.this, "Image Upload Failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) { }
                }).dispatch();
    }

    private void saveUserData() {
        String username = usernameEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Create User Info Object
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("email", currentUser.getEmail()); // Keep existing email
        if (!profileImageUrl.isEmpty()) {
            userInfo.put("profileImage", profileImageUrl); // Only update image if new one uploaded
        }

        // Save Data to Firebase
        userRef.setValue(userInfo)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(UserProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    fetchUserInfo(); // Refresh UI after saving data
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserProfile.this, "Failed to Update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
        progressBar.setVisibility(View.GONE);
    }

    private void fetchUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(UserProfile.this, "No User Data Found!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Retrieve user data
                UsersInfo userInfo = snapshot.getValue(UsersInfo.class);
                if (userInfo != null) {
                    usernameEditText.setText(userInfo.username);
                    emailEditText.setText(userInfo.email);
                    progressBar.setVisibility(View.GONE);


                }

                // Load Profile Image if available
                if (snapshot.hasChild("profileImage")) {
                    String profileImage = snapshot.child("profileImage").getValue(String.class);
                    if (profileImage != null && !profileImage.isEmpty()) {
                        Picasso.get().load(profileImage).into(imageView);
                    }
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
