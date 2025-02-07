package unigo.bd;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateSchedule extends AppCompatActivity {

    private Spinner spinnerRoutes, spinnerBusNumbers;
    private EditText editTextTime;
    private Button btnUpdate;
    private ImageButton backButton;

    private DatabaseReference databaseReference;
    private ArrayList<String> routesList = new ArrayList<>();
    private ArrayList<String> busNumbersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerRoutes = findViewById(R.id.spinnerRoutes);
        spinnerBusNumbers = findViewById(R.id.spinnerBusNumbers);
        editTextTime = findViewById(R.id.editTextTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        backButton = findViewById(R.id.back_button);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        loadRoutes();
        loadBusNumbers();

        // Handle Time Picker
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        backButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String scheduleId = intent.getStringExtra("SCHEDULE_ID");
        String category = intent.getStringExtra("CATEGORY");

        if (scheduleId != null) {
            // Pre-fill the fields with data for editing
            spinnerRoutes.setSelection(routesList.indexOf(intent.getStringExtra("ROUTE")));
            spinnerBusNumbers.setSelection(busNumbersList.indexOf(intent.getStringExtra("BUS")));
            editTextTime.setText(intent.getStringExtra("TIME"));

            // Update Firebase on save
            btnUpdate.setOnClickListener(v -> {
                String selectedRoute = spinnerRoutes.getSelectedItem().toString();
                String selectedBusNumber = spinnerBusNumbers.getSelectedItem().toString();
                String selectedTime = editTextTime.getText().toString();

                Schedule updatedSchedule = new Schedule(selectedRoute, selectedBusNumber, selectedTime);
                databaseReference.child(category).child(currentDate).child(scheduleId).setValue(updatedSchedule)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Schedule updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update schedule", Toast.LENGTH_SHORT).show());
            });
        }
    }

    private void loadRoutes() {
        // Manually set the routes
        routesList.clear();
        routesList.add("Route 1");
        routesList.add("Route 2");
        routesList.add("Route 3");
        routesList.add("Route 4");

        // Set the adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoutes.setAdapter(adapter);
    }

    private void loadBusNumbers() {
        // Load bus numbers from Firebase
        databaseReference.child("buses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                busNumbersList.clear();
                for (DataSnapshot busSnapshot : snapshot.getChildren()) {
                    Bus bus = busSnapshot.getValue(Bus.class);
                    if (bus != null) {
                        busNumbersList.add(bus.getBusNumber()); // Add only the bus number
                    }
                }
                if (!busNumbersList.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateSchedule.this, android.R.layout.simple_spinner_item, busNumbersList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBusNumbers.setAdapter(adapter);
                } else {
                    Toast.makeText(UpdateSchedule.this, "No bus numbers found in Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateSchedule.this, "Failed to load bus numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) ->
                editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute1)), hour, minute, true);
        timePickerDialog.show();
    }


}
