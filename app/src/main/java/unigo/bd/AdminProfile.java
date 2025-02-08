package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfile extends AppCompatActivity {
    private EditText username, currentPassword, newPassword;
    private Button save;
    private TextView back;
    private ProgressBar progressBar;
    private DatabaseReference adminRef;
    private String currentAdminEmail, currentAdminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        InitializeUI();

        // Initialize Firebase Database reference
        adminRef = FirebaseDatabase.getInstance().getReference("AdminInfo");
        fetchAdminCredentials();
        back.setOnClickListener(v -> finish());

        save.setOnClickListener(v -> {
            String enteredUsername = username.getText().toString().trim();
            String enteredCurrentPassword = currentPassword.getText().toString().trim();
            String enteredNewPassword = newPassword.getText().toString().trim();

            // Validate input
            if (!validateInput(enteredUsername, enteredCurrentPassword, enteredNewPassword)) {
                return;
            }

            // Check if the entered current password matches the one in Firebase
            if (!enteredCurrentPassword.equals(currentAdminPassword)) {
                currentPassword.setError("Incorrect Current Password!");
                currentPassword.requestFocus();
                return;
            }

            // Ensure new password is different from the current one
            if (enteredNewPassword.equals(currentAdminPassword)) {
                newPassword.setError("New password cannot be the same as the current password!");
                newPassword.requestFocus();
                return;
            }

            // Check if username has changed
            if (enteredUsername.equals(currentAdminEmail)) {
                Toast.makeText(this, "Email has not changed!", Toast.LENGTH_SHORT).show();
            }

            // Update credentials in Firebase
            updateAdminCredentials(enteredUsername, enteredNewPassword);
        });
    }

    private void InitializeUI() {
        username = findViewById(R.id.et_username);
        currentPassword = findViewById(R.id.et_current_password);
        newPassword = findViewById(R.id.et_new_password);
        save = findViewById(R.id.btn_save_changes);
        back = findViewById(R.id.btn_back_adminProfile);
        progressBar = findViewById(R.id.progressBar_adminProfile);
    }

    private void fetchAdminCredentials() {
        progressBar.setVisibility(View.VISIBLE);
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentAdminEmail = snapshot.child("email").getValue(String.class);
                    currentAdminPassword = snapshot.child("password").getValue(String.class);

                    username.setText(currentAdminEmail);
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminProfile.this, "Admin credentials not found!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminProfile.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdminCredentials(String newUsername, String newPassword) {
        progressBar.setVisibility(View.VISIBLE);
        adminRef.child("email").setValue(newUsername);
        adminRef.child("password").setValue(newPassword)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminProfile.this, "Admin credentials updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminProfile.this, "Failed to update admin credentials.", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput(String username, String currentPassword, String newPassword) {
        boolean isValid = true;

        if (username.isEmpty()) {
            this.username.setError("Username is empty");
            this.username.requestFocus();
            isValid = false;
        }
        if (currentPassword.isEmpty()) {
            this.currentPassword.setError("Enter current password");
            this.currentPassword.requestFocus();
            isValid = false;
        }
        if (newPassword.isEmpty()) {
            this.newPassword.setError("Enter new password");
            this.newPassword.requestFocus();
            isValid = false;
        }
        return isValid;
    }
}
