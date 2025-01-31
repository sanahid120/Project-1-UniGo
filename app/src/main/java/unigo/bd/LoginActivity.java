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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private ProgressBar progressBar;
    private TextView register;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        email = findViewById(R.id.et_login_email);
        password = findViewById(R.id.et_login_password);
        login = findViewById(R.id.btn_loginPage_signin);
        register = findViewById(R.id.tv_loginPage_register);
        progressBar = findViewById(R.id.login_progressbar);
        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference("AdminInfo");
        userRef.setValue(new AdminInfo("Admin@gmail.com","AdminPassword"));

        // Login button click listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                // Attempt to sign in the user
                mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Check if the user's email is verified
                            if(isAdmin(mail,pass)){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Admin LogIn...", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                // Navigate to the next activity if verified
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Login Succesfull!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, UserHomepage.class));
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private boolean isAdmin(String mail, String pass) {

        return true;
    }
}