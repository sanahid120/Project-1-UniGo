package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {


    private EditText email, password,Username,confirmedPassword;
    private Button register;
    private TextView login;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    Pattern emailPattern = Pattern.compile("^[a-z0-9_]+@lus\\.ac\\.bd$");
    Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI elements
        email = findViewById(R.id.et_signup_email);
        password = findViewById(R.id.et_signup_password);
        register = findViewById(R.id.btn_registrationPage_signup);
        login = findViewById(R.id.tv_registrationPage_login);
        Username = findViewById(R.id.et_signup_username);
        confirmedPassword = findViewById(R.id.et_signup_confirmedPassword);
        progressBar = findViewById(R.id.signup_progressbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference("userInfo");

        // Navigate to login page
        login.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

        // Register button click listener
        register.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmedpass = confirmedPassword.getText().toString().trim();
            String username = Username.getText().toString().trim();

            // Validate email and password fields
            if(username.isEmpty()){
                Username.setError("Username field required");
                Username.requestFocus();
                return;
            }

            if (mail.isEmpty()) {
                email.setError("Email field is empty");
                email.requestFocus();
                return;
            }

            if (!emailPattern.matcher(mail).matches()) {
                Toast.makeText(this, "Unexpected e-mail address,LU mail only", Toast.LENGTH_SHORT).show();
                email.setError("Invalid email format");
                email.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                password.setError("Password field is empty");
                password.requestFocus();
                return;
            }

            if (!passwordPattern.matcher(pass).matches()) {
                Toast.makeText(this, "Password must have least of 8 characters", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Use Uppercase, lowercase, Number, Sp. Character", Toast.LENGTH_SHORT).show();                password.setError("invalid password format");
                password.requestFocus();
                return;
            }

            if(!pass.equals(confirmedpass)){
                password.setError("Password and confirmed password doesn't match");
                password.requestFocus();
                confirmedPassword.setError("Password and Confirmed password doesn't match");
                confirmedPassword.requestFocus();
                return;
            }

            // Attempt to create a new user
            mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(verificationTask -> {
                            if (verificationTask.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Verification email sent! Please check your inbox.", Toast.LENGTH_SHORT).show();
                                String userId = mAuth.getCurrentUser().getUid();
                                UsersInfo user = new UsersInfo(username, mail);
                                userRef.child(userId).setValue(user).addOnCompleteListener(Task->{
                                    if(Task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Failed to save user data: " + Task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                                mAuth.signOut();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE );
                                Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignupActivity.this, "User already exists! Please log in.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE );
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE );
                        }
                    }
                }
            });
        });
    }
}