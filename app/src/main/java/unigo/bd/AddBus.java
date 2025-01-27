package unigo.bd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddBus extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView busListRecyclerView;
    private BusAdapter busAdapter;
    private ImageButton addButton;
    private ImageButton backButton;

    private DatabaseReference databaseReference;
    private ArrayList<String> busList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        busListRecyclerView = findViewById(R.id.bus_list);
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.back_button);

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
        busAdapter =new BusAdapter(busList, this::showUpdateDeleteDialog);
        busListRecyclerView.setAdapter(busAdapter);

        // Floating action button click handler
        addButton.setOnClickListener(v -> showAddBusDialog());

        // Load bus data from Firebase
        loadBusData();
    }

    private void showUpdateDeleteDialog(String s) {
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
                    } else {
                        Toast.makeText(this, "Bus Number cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null) // `null` automatically cancels the dialog
                .show();
    }


    private void addBusToFirebase(String busNumber) {
        String busId = databaseReference.push().getKey();
        if (busId != null) {
            databaseReference.child(busId).setValue(busNumber).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Bus Number Added: " + busNumber, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add bus!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadBusData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                busList.clear();
                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    String busNumber = busSnapshot.getValue(String.class);
                    busList.add(busNumber);
                }
                busAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddBus.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDeleteDialog(String busId, String currentBusNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update/Delete Bus Number");

        // Input field for updating
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentBusNumber);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedBusNumber = input.getText().toString().trim();
            if (!updatedBusNumber.isEmpty()) {
                updateBusInFirebase(busId, updatedBusNumber);
            } else {
                Toast.makeText(this, "Bus Number cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Delete", (dialog, which) -> deleteBusFromFirebase(busId));

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateBusInFirebase(String busId, String updatedBusNumber) {
        databaseReference.child(busId).setValue(updatedBusNumber).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Bus Number Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update bus!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBusFromFirebase(String busId) {
        databaseReference.child(busId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Bus Number Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete bus!", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(AddBus.this,UserHomepage.class));
        } else if (itemId == R.id.update_add_bus) {
            Toast.makeText(this, "Select a bus to update.", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.delete_add_bus) {
            Toast.makeText(this, "Select a bus to delete.", Toast.LENGTH_SHORT).show();
        }
        else if (itemId == R.id.logout) {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddBus.this,UserHomepage.class));
            finish();
        }
        return true;
    }

}
