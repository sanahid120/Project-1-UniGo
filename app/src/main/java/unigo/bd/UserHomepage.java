package unigo.bd;
import static unigo.bd.R.id.login_menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserHomepage extends AppCompatActivity {
private Button stuff,student,faculty,notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        student = findViewById(R.id.btnStudent);
        stuff = findViewById(R.id.btnStuff);
        faculty = findViewById(R.id.btnFaculty);
        notice = findViewById(R.id.btnNotice);
        Toolbar toolbar = findViewById(R.id.topBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {   // remove the text from topBar of xml
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        stuff.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked Stuff Button!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this, User_Schedule.class));
        });
        student.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked Student Button!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this,Admin_Schedule.class));
        });
        faculty.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked Faculty Button!", Toast.LENGTH_SHORT).show();
        });
        notice.setOnClickListener(v->{
            Toast.makeText(this, "You Clicked Notice Button!", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== login_menu){
            Toast.makeText(this, "Login/Register Button Selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHomepage.this,SignupActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
