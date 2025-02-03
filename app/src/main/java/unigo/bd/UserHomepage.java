package unigo.bd;
import static unigo.bd.R.id.login_menu_user;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class UserHomepage extends AppCompatActivity {
private Button schedule,notice;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.getUserType().equals("admin")) {
                startActivity(new Intent(this, Admin_Homepage.class));
                finish();
            }
            else if(!sessionManager.isLoggedIn()&&sessionManager.getUserType().equals("admin")){
                startActivity(new Intent(this,UserHomepage.class));
            }
        }

        schedule = findViewById(R.id.btnSchedule);
        notice = findViewById(R.id.btnNotice);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        schedule.setOnClickListener(v->{
            Toast.makeText(this, "Navigating to Schedule...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this, User_Schedule.class));
        });
        notice.setOnClickListener(v->{
            Toast.makeText(this, "Navigating to NoticeBoard...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this, UserNoticeBoard.class));

        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        SessionManager sessionManager = new SessionManager(this);

        MenuItem loginItem = menu.findItem(login_menu_user);
        MenuItem logoutItem = menu.findItem(R.id.logout_user_menu);
        MenuItem requestBusItem = menu.findItem(R.id.requestBus_user_menu);

        if (sessionManager.isLoggedIn()) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            requestBusItem.setVisible(true);
        } else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            requestBusItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== login_menu_user){
            Toast.makeText(this, "Login/Register Button Selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this,LoginActivity.class));
            return true;
        } else if (item.getItemId()==R.id.logout_user_menu) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, UserHomepage.class));
            finish();
        }
        else if (item.getItemId()==R.id.requestBus_user_menu);{
            Toast.makeText(this, "You Clicked Request Bus...", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
