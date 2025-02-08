package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminNoticeBoard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoticeAdapter adapter;
    private List<Notice> noticeList;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notice_board);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar_AdminNoticeBoard);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ImageButton back = findViewById(R.id.btnBack);back.setOnClickListener(v-> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        adapter = new NoticeAdapter(this, noticeList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Notices");

        fetchNotices();
    }

    private void fetchNotices() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                if (!snapshot.exists()) { // No schedules found
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminNoticeBoard.this, "No Notice Available!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notice notice = data.getValue(Notice.class);
                    noticeList.add(notice);
                }
                Collections.reverse(noticeList);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminNoticeBoard.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_noticeboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.id_logout){
            Toast.makeText(this, "logging Out...", Toast.LENGTH_SHORT).show();
            new SessionManager(this).logout();
            startActivity(new Intent(this,UserHomepage.class));
            return true;
        }
        else if (item.getItemId()==R.id.id_addBus_adminNoticeBoard) {
            startActivity(new Intent(this,AddBus.class));
            return true;
        }else if (item.getItemId()==R.id.id_addNotice_AdminNoticeBoard) {
            startActivity(new Intent(this, AddNotice.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
