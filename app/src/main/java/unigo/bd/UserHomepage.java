package unigo.bd;

import static unigo.bd.R.id.login_menu;
import static unigo.bd.R.id.register_menu;

import android.annotation.SuppressLint;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== login_menu){
            Toast.makeText(this, "Login Button Selected", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(item.getItemId()==register_menu){
            Toast.makeText(this, "Register Button Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
