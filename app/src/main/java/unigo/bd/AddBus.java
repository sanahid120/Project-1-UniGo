package unigo.bd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddBus extends AppCompatActivity {

    private RecyclerView busListRecyclerView;
    private BusAdapter busAdapter;
    private FloatingActionButton addButton;
    private ImageButton backButton;
    ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private ArrayList<Bus> busList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        busListRecyclerView = findViewById(R.id.bus_list);
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.addBus_progressBar);

        // Set up Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("buses");
        busList = new ArrayList<>();

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Set up RecyclerView
        busListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        busAdapter =new BusAdapter(busList);
        busListRecyclerView.setAdapter(busAdapter);

        // Floating action button click handler
        addButton.setOnClickListener(v -> showAddBusDialog());

        // Load bus data from Firebase
        loadBusData();
    }
    private void showAddBusDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle("Add Bus Number")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String busNumber = input.getText().toString().trim();
                    if (!busNumber.isEmpty()) {
                        addBusToFirebase(busNumber);
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "Bus Number cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null) // `null` automatically cancels the dialog
                .show();
    }


    private void addBusToFirebase(String busNumber) {
        String busId = databaseReference.push().getKey(); // Generate a unique ID
        if (busId != null) {
            Bus newBus = new Bus(busNumber); // Create a new Bus object
            newBus.setId(busId); // Set the generated ID
            databaseReference.child(busId).setValue(newBus).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Bus Added: " + busNumber, Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to add bus!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    private void loadBusData() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                busList.clear();
                if (!snapshot.exists()) { // No schedules found
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddBus.this, "No Schedule Found!", Toast.LENGTH_SHORT).show();
                    busAdapter.notifyDataSetChanged();
                    return;
                }
                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    Bus bus = busSnapshot.getValue(Bus.class); // Map data to Bus class
                    if (bus != null) {
                        busList.add(bus);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                busAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddBus.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_bus_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuSelection(item.getItemId());
    }

    private boolean handleMenuSelection(int itemId) {
        if (itemId == R.id.home_from_addBus) {
            startActivity(new Intent(AddBus.this,Admin_Homepage.class));
        }
        else if (itemId == R.id.logout) {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            new SessionManager(this).logout();
            startActivity(new Intent(AddBus.this,UserHomepage.class));
            finish();
        }
        return true;
    }
}