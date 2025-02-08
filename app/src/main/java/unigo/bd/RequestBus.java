package unigo.bd;

import static unigo.bd.R.id.id_logout_RequestBus;
import static unigo.bd.R.id.login_menu_user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestBus extends AppCompatActivity {
    private Spinner spinnerTime, spinnerRoute;
    private Button btnSubmitRequest;
    private ImageButton back;
    private ProgressBar progressBar;
    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> routesList = new ArrayList<>();
    private List<BusRequestModel> requestList = new ArrayList<>();

    private DatabaseReference dbRef;
    private String userId, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_bus);

        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerRoute = findViewById(R.id.spinnerRoutes);
        btnSubmitRequest = findViewById(R.id.btnUploadRequest);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.btnBack);
        back.setOnClickListener(v-> finish());
        dbRef = FirebaseDatabase.getInstance().getReference("BusRequests");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        btnSubmitRequest.setOnClickListener(v -> checkAndSubmitRequest());
        loadTime();
        loadRoutes();
    }
    private void loadTime() {
        // Manually set the routes
        timeList.clear();
        timeList.add("12:45 PM");
        timeList.add("3:00 PM");

        // Set the adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);
    }
    private void loadRoutes() {
        // Manually set the routes
        routesList.clear();
        routesList.add("Route-1");
        routesList.add("Route-2");
        routesList.add("Route-3");
        routesList.add("Route-4");

        // Set the adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoute.setAdapter(adapter);
    }

    private void checkAndSubmitRequest() {
        progressBar.setVisibility(View.VISIBLE);
        String selectedTime = spinnerTime.getSelectedItem().toString();
        String selectedRoute = spinnerRoute.getSelectedItem().toString();

        DatabaseReference userRef = dbRef.child(currentDate).child(selectedTime).child(selectedRoute).child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RequestBus.this, "You have already requested a bus today!", Toast.LENGTH_SHORT).show();
                } else {
                    submitRequest(selectedTime, selectedRoute);
                    Toast.makeText(RequestBus.this, "Request Uploaded!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void submitRequest(String time, String route) {
        dbRef.child(currentDate).child(time).child(route).child("count")
                .setValue(ServerValue.increment(1));
        dbRef.child(currentDate).child(time).child(route).child("users")
                .child(userId).setValue(true);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.request_bus_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== id_logout_RequestBus){
            Toast.makeText(this, "logout Button Selected", Toast.LENGTH_SHORT).show();
            new SessionManager(this).logout();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(RequestBus.this,UserHomepage.class));
            finish();
            return true;
        } else if (item.getItemId()==R.id.id_Home_fromRequestBus) {
            startActivity(new Intent(this, UserHomepage.class));
        }
        else if (item.getItemId()==R.id.id_ViewRequest_RequestBus);{
            startActivity(new Intent(this, ViewRequestBus.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
