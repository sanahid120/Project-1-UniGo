package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Admin_Homepage extends AppCompatActivity {
    private Button viewRequests,schedule,addBus,notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);
        schedule = findViewById(R.id.btnSchedule);
        addBus = findViewById(R.id.btnAddBus);
        notice = findViewById(R.id.adminNoticeButton);
        viewRequests = findViewById(R.id.adminViewRequest);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        schedule.setOnClickListener(v->{
            startActivity(new Intent(Admin_Homepage.this,Admin_Schedule.class));
        });
        addBus.setOnClickListener(v->{
            startActivity(new Intent(Admin_Homepage.this,AddBus.class));
        });
        notice.setOnClickListener(v->{
            startActivity(new Intent(Admin_Homepage.this,AdminNoticeBoard.class));
        });
        viewRequests.setOnClickListener(v->{
            startActivity(new Intent(Admin_Homepage.this,ViewRequestBus.class));
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.id_logout){
            Toast.makeText(this, "logging Out...", Toast.LENGTH_SHORT).show();
            new SessionManager(Admin_Homepage.this).logout();
            startActivity(new Intent(Admin_Homepage.this,UserHomepage.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}