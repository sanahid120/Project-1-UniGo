package unigo.bd;

import static unigo.bd.R.id.login_menu;
import static unigo.bd.R.id.toolbar;
import static unigo.bd.R.id.topBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class User_Schedule extends AppCompatActivity {

    private RecyclerView recyclerViewSchedule;
    private Adapter_UserSchedule scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private DatabaseReference databaseReference;
    private String globalCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_schedule); // Correct XML layout
        ImageButton btnBack = findViewById(R.id.back_button);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        TextView scheduleTitle = findViewById(R.id.scheduleTitle);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Back button functionality
        btnBack.setOnClickListener(v -> finish());

        // Display current date
        displayCurrentDateTime(scheduleTitle);

        // RecyclerView setup
        recyclerViewSchedule = findViewById(R.id.recyclerView);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>();
        scheduleAdapter = new Adapter_UserSchedule(scheduleList);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Spinner selection listener
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalCategory = parent.getItemAtPosition(position).toString();
                fetchSchedules(globalCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                globalCategory = "Student"; // Default to "Student"
                fetchSchedules(globalCategory);
            }
        });
    }

    private void fetchSchedules(String category) {
        databaseReference.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Schedule schedule = data.getValue(Schedule.class);
                    if (schedule != null) {
                        scheduleList.add(new ScheduleItem(schedule.route, schedule.time, schedule.busNumber, data.getKey()));
                    }
                }
                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log or handle errors
            }
        });
    }

    private void displayCurrentDateTime(TextView scheduleTitle) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
        String formattedDate = formatter.format(Calendar.getInstance().getTime());
        scheduleTitle.setText(formattedDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_schedule_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== login_menu){
            Toast.makeText(this, "Login/Register Button Selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(User_Schedule.this,SignupActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
