package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private ProgressBar progressBar;
    private TextView register;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager= new SessionManager(LoginActivity.this);
        // Initialize UI elements
        email = findViewById(R.id.et_login_email);
        password = findViewById(R.id.et_login_password);
        login = findViewById(R.id.btn_loginPage_signin);
        register = findViewById(R.id.tv_loginPage_register);
        progressBar = findViewById(R.id.login_progressbar);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database Reference
        userRef = FirebaseDatabase.getInstance().getReference("AdminInfo");

        // Login button click listener
        login.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // Validate email and password fields
            if (mail.isEmpty()) {
                email.setError("Email field is empty");
                email.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                password.setError("Password field is empty");
                password.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            // Check if user is an admin
            checkIfAdmin(mail, pass);
        });

        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }


    private void checkIfAdmin(String mail, String pass) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String adminEmail = snapshot.child("email").getValue(String.class);
                    String adminPassword = snapshot.child("password").getValue(String.class);

                    if (adminEmail != null && adminPassword != null && adminEmail.equals(mail) && adminPassword.equals(pass)) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                        sessionManager.saveLoginState("admin");
                        startActivity(new Intent(LoginActivity.this, Admin_Homepage.class));
                        finish();
                    } else {
                        // If not admin, proceed with normal user authentication
                        loginUser(mail, pass);
                    }
                } else {
                    // If AdminInfo node is missing, proceed with normal authentication
                    loginUser(mail, pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error checking admin credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Logs in a regular user with Firebase Authentication.
     */
    private void loginUser(String mail, String pass) {
        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    sessionManager.saveLoginState("user");
                    startActivity(new Intent(LoginActivity.this, UserHomepage.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
