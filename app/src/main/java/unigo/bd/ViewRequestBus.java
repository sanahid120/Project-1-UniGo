package unigo.bd;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewRequestBus extends AppCompatActivity {
    ImageButton back;
    private ProgressBar progressBar;
    private RecyclerView recyclerRequests;
    private BusRequest_Adapter adapter;
    private List<BusRequestModel> requestList = new ArrayList<>();

    private DatabaseReference dbRef;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_bus);

        progressBar = findViewById(R.id.progressBar);
        recyclerRequests = findViewById(R.id.recyclerView_BusRequestview);
        back = findViewById(R.id.btnBack);
        back.setOnClickListener(v-> finish());

        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BusRequest_Adapter(requestList,this);
        recyclerRequests.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("BusRequests");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        loadRequests();
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        dbRef.child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                if(!snapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ViewRequestBus.this, "No Schedule Found!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }
                for (DataSnapshot timeSlot : snapshot.getChildren()) {
                    for (DataSnapshot route : timeSlot.getChildren()) {
                        long count = route.child("count").getValue(Long.class) != null ? route.child("count").getValue(Long.class) : 0;
                        requestList.add(new BusRequestModel(timeSlot.getKey(), route.getKey(), count));
                        progressBar.setVisibility(View.GONE);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ViewRequestBus.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                return;
            }
        });
    }
}
