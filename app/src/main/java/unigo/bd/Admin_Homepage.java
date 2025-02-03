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
    private Button stuff,schedule,addBus,notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);
        schedule = findViewById(R.id.btnSchedule);
        addBus = findViewById(R.id.btnAddBus);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        schedule.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked Schedule Button!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Homepage.this,Admin_Schedule.class));
        });
        addBus.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked AddBus Button!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Homepage.this,AddBus.class));
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
        } else if (item.getItemId()==R.id.id_userHomepage) {
            Toast.makeText(this, "Navigating to User...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Homepage.this,UserHomepage.class));
            return true;
        }else if (item.getItemId()==R.id.id_addBus) {
            Toast.makeText(this, "Navigating to AddBus...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Homepage.this,AddBus.class));
            return true;
        }else if (item.getItemId()==R.id.id_noticeBoard) {
            Toast.makeText(this, "Navigating to NoticeBoard...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin_Homepage.this, AdminNoticeBoard.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}