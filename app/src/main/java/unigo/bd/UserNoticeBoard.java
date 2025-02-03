package unigo.bd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class UserNoticeBoard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserNoticeBoardAdapter adapter;
    private List<Notice> noticeList;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice_board);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar_AdminNoticeBoard);
        ImageButton back = findViewById(R.id.btnBack);back.setOnClickListener(v-> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        adapter = new UserNoticeBoardAdapter(this, noticeList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Notices");

        fetchNotices();
    }

    private void fetchNotices() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();  // Clear previous data
                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserNoticeBoard.this, "No Notices Found!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();  // Ensure RecyclerView updates even if empty
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    Notice notice = data.getValue(Notice.class);
                    if (notice != null) {
                        noticeList.add(notice);
                    }
                }

                adapter.notifyDataSetChanged();  // Refresh the RecyclerView
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserNoticeBoard.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
