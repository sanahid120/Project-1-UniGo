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
import java.util.ArrayList;
import java.util.Calendar;

public class AddSchedule extends AppCompatActivity {

    private Spinner spinnerRoutes, spinnerBusNumbers;
    private EditText editTextTime;
    private Button btnCreate;
    private RadioGroup radioGroupFor;
    private ImageButton backButton;

    private DatabaseReference databaseReference;
    private ArrayList<String> routesList = new ArrayList<>();
    private ArrayList<String> busNumbersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerRoutes = findViewById(R.id.spinnerRoutes);
        spinnerBusNumbers = findViewById(R.id.spinnerBusNumbers);
        editTextTime = findViewById(R.id.editTextTime);
        btnCreate = findViewById(R.id.btnCreate);
        radioGroupFor = findViewById(R.id.radioGroupFor);
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

        // Handle Create Button
        btnCreate.setOnClickListener(v -> saveScheduleToFirebase());

        // Handle Back Button
        backButton.setOnClickListener(v -> finish());
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
                    String busNumber = busSnapshot.getValue(String.class);
                    if (busNumber != null) {
                        busNumbersList.add(busNumber);
                    }
                }
                if (!busNumbersList.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddSchedule.this, android.R.layout.simple_spinner_item, busNumbersList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBusNumbers.setAdapter(adapter);
                } else {
                    Toast.makeText(AddSchedule.this, "No bus numbers found in Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddSchedule.this, "Failed to load bus numbers", Toast.LENGTH_SHORT).show();
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

    private void saveScheduleToFirebase() {
        String selectedFor = ((RadioButton) findViewById(radioGroupFor.getCheckedRadioButtonId())).getText().toString();
        String selectedRoute = spinnerRoutes.getSelectedItem().toString();
        String selectedBusNumber = spinnerBusNumbers.getSelectedItem().toString();
        String selectedTime = editTextTime.getText().toString();

        if (selectedRoute.isEmpty() || selectedBusNumber.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference scheduleRef = databaseReference.child("Schedules").child(selectedFor);

        // Create a unique schedule ID
        String scheduleId = scheduleRef.push().getKey();

        if (scheduleId != null) {
            Schedule schedule = new Schedule(selectedRoute, selectedBusNumber, selectedTime);
            scheduleRef.child(scheduleId).setValue(schedule)
                    .addOnSuccessListener(aVoid -> Toast.makeText(AddSchedule.this, "Schedule added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AddSchedule.this, "Failed to add schedule", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Error: Unable to generate schedule ID", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_schedule_menu, menu);
        return true;
    }
    private void restartActivity() {
        // Restart the current activity
        Intent intent = getIntent();
        finish(); // Finish the current instance
        startActivity(intent); // Start a new instance
    }

}
