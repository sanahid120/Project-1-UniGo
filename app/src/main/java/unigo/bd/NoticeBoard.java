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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class NoticeBoard extends AppCompatActivity {
    private ImageView imageView;
    private static final int IMAGE_REQ = 1;
    private EditText titleEditText, descriptionEditText;
    private String title, description, imageUrl;
    private Uri imagePath;
    private Button uploadButton;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        imageView = findViewById(R.id.imageView_notice);
        titleEditText = findViewById(R.id.et_NoticeTitle);
        descriptionEditText = findViewById(R.id.et_noticeContent);
        uploadButton = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar_AdminNoticeBoard);
        reference = FirebaseDatabase.getInstance().getReference().child("Notices");

        // Initialize Cloudinary
        initCloudinary();

        // Select Image
        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(NoticeBoard.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(NoticeBoard.this, Manifest.permission.READ_MEDIA_IMAGES)
                            == PackageManager.PERMISSION_GRANTED) {

                pickImage();

            } else {
                ActivityCompat.requestPermissions(NoticeBoard.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES}, IMAGE_REQ);
            }
        });


        // Upload Notice
        uploadButton.setOnClickListener(v -> uploadNotice());
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

    private void uploadNotice() {
        title = titleEditText.getText().toString().trim();
        description = descriptionEditText.getText().toString().trim();

        // Check required fields
        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }
        if (imagePath == null && description.isEmpty()) {
            Toast.makeText(this, "Either description or image must be provided", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (imagePath != null) {
            uploadImageToCloudinary();
        } else {
            uploadDataToFirebase(null);
        }
    }

    private void uploadImageToCloudinary() {
        MediaManager.get().upload(imagePath)
                .unsigned("SANAHID") // If using unsigned upload, replace with your upload preset
                .option("resource_type", "image")
                .option("folder", "Notices")
                .constrain(TimeWindow.immediate())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = (String) resultData.get("secure_url");
                        uploadDataToFirebase(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NoticeBoard.this, "Image Upload Failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) { }
                }).dispatch();
    }

    private void uploadDataToFirebase(String imageUrl) {
        String key = reference.push().getKey();
        Notice notice = new Notice(title, description, imageUrl, key);

        reference.child(key).setValue(notice)
                .addOnSuccessListener(unused -> {
                    titleEditText.setText("");
                    descriptionEditText.setText("");
                    imagePath = null;
                    imageView.setImageResource(R.drawable.baseline_upload_24);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NoticeBoard.this, "Notice Added Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NoticeBoard.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();

            if (imagePath != null) {
                imageView.setImageURI(imagePath);  // Show the selected image in ImageView
            } else {
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
