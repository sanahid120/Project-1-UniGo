package unigo.bd;

import static unigo.bd.R.id.login_menu;
import static unigo.bd.R.id.logout;
import static unigo.bd.R.id.menu_logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Admin_Schedule extends AppCompatActivity implements ScheduleAdapter.OnScheduleActionListener {

    private RecyclerView recyclerViewSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private DatabaseReference databaseReference;
    public String globalCatagory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);

        // Initialize views
        TextView btnAdd = findViewById(R.id.addButton);
        Button btnMarkCompleted = findViewById(R.id.markCompletedButton);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());

        // RecyclerView setup
        recyclerViewSchedule = findViewById(R.id.recyclerView);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        // Initialize list and adapter
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList, this);
        recyclerViewSchedule.setAdapter(scheduleAdapter);
        displayCurrentDateTime();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                if (category.equals("Student")) {
                    fetchSchedules("Student");
                } else if (category.equals("Teacher")) {
                    fetchSchedules("Teacher");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchSchedules("Student");
            }
        });

        // Add new schedule
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_Schedule.this, AddSchedule.class);
            startActivity(intent);
        });

        // Mark completed
        btnMarkCompleted.setOnClickListener(v -> {
            for (ScheduleItem schedule : scheduleList) {
                if (schedule.isMarkedCompleted()) {
                    databaseReference.child(schedule.getId()).removeValue();
                }
            }
            fetchSchedules(globalCatagory);
        });
    }

    private void fetchSchedules(String category) {
        globalCatagory=category;
        databaseReference.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Schedule schedule = data.getValue(Schedule.class);
                    if (schedule != null) {
                        ScheduleItem scheduleItem = new ScheduleItem(
                                schedule.route,
                                schedule.time,
                                schedule.busNumber
                                );
                        scheduleItem.setId(data.getKey());
                        scheduleList.add(scheduleItem);
                    }
                }
                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void displayCurrentDateTime() {
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd,yyyy", Locale.getDefault());
        String formattedDateTime = formatter.format(calendar.getTime());

        // Display it in a TextView
        TextView scheduleTitle = findViewById(R.id.scheduleTitle);
        scheduleTitle.setText(formattedDateTime);
    }

    @Override
    public void onMarkCompleted(ScheduleItem scheduleItem, boolean isChecked) {
        scheduleItem.setMarkedCompleted(isChecked);
        databaseReference.child(scheduleItem.getId()).child("Schedule").child(globalCatagory);
    }

    @Override
    public void onDeleteSchedule(ScheduleItem scheduleItem) {
        databaseReference.child(scheduleItem.getId()).removeValue();
        fetchSchedules(globalCatagory); // Refresh the list after deletion
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== menu_logout){
            Toast.makeText(this, "Logging Out...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Schedule.this,SignupActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
