package unigo.bd;

import static unigo.bd.R.id.id_ViewRequest_AdminSchedule;
import static unigo.bd.R.id.id_addBus_Schedule;
import static unigo.bd.R.id.id_logout_Schedule;
import static unigo.bd.R.id.id_noticeBoard_Schedule;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Admin_Schedule extends AppCompatActivity {

    private RecyclerView recyclerViewSchedule;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private DatabaseReference databaseReference;
    public String globalCatagory;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;  // Add this

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule);

        // Initialize views
        TextView btnAdd = findViewById(R.id.addButton);
        Button btnDeleteAll = findViewById(R.id.markCompletedButton);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        toolbar =findViewById(R.id.topBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initialize

        progressBar=findViewById(R.id.adminSchedule_progressBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchSchedules(globalCatagory);
        });
        // RecyclerView setup
        recyclerViewSchedule = findViewById(R.id.recyclerView);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        // Initialize list and adapter
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList);
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

        btnDeleteAll.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(Admin_Schedule.this)
                    .setTitle("Confirm Action")
                    .setMessage("Are you sure you want to delete All schedules?.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        progressBar.setVisibility(View.VISIBLE);
                        deleteAllSchedules(globalCatagory);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void deleteAllSchedules(String globalCatagory) {
        DatabaseReference deleteReference = FirebaseDatabase.getInstance().getReference();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        deleteReference.child(globalCatagory).child(currentDate).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(Admin_Schedule.this, "All "+globalCatagory+" schedules deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Admin_Schedule.this, "Failed to delete "+globalCatagory+" schedules", Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.GONE);
        scheduleList.clear();
        scheduleAdapter.notifyDataSetChanged();
    }


    private void fetchSchedules(String category) {
        globalCatagory = category;
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);  // Show refreshing animation

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        databaseReference.child(category).child(currentDate).orderByChild("route").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear(); // Clear list before adding new data

                if (!snapshot.exists()) { // No schedules found
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Admin_Schedule.this, "No Schedule Found!", Toast.LENGTH_SHORT).show();
                    scheduleAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);  // Show refreshing animation

                    return;
                }

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
                    swipeRefreshLayout.setRefreshing(false);  // Show refreshing animation

                }
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);  // Show refreshing animation

                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Admin_Schedule.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== id_logout_Schedule){
            Toast.makeText(this, "Logging Out...", Toast.LENGTH_SHORT).show();
            new SessionManager(this).logout();
            startActivity(new Intent(Admin_Schedule.this,UserHomepage.class));
            return true;
        } else if (item.getItemId()== id_noticeBoard_Schedule){
            Toast.makeText(this, "Navigating to NoticeBoard...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Schedule.this, AdminNoticeBoard.class));
            return true;
        }else if (item.getItemId()== id_addBus_Schedule){
            Toast.makeText(this, "Navigating to AddBus...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Schedule.this,AddBus.class));
            return true;
        }else if (item.getItemId()== id_ViewRequest_AdminSchedule){
            Toast.makeText(this, "Navigating to ViewRequest...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Schedule.this,ViewRequestBus.class));
            return true;
        }
        else{
            startActivity(new Intent(Admin_Schedule.this,Admin_Homepage.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
