package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView appNameText;
    private String appName = "UniGo";
    private int index = 0;
    private long delay = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            initConfig();
//        } catch (Exception e) {
//            Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
        appNameText = findViewById(R.id.app_name_text);
        animateText();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, UserHomepage.class)); // Replace with your main activity
            finish();
        }, 3000);
    }


    private void animateText() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < appName.length()) {
                    appNameText.setText(appNameText.getText().toString() + appName.charAt(index));
                    index++;
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);

    }
//
//    private void initConfig() {
//        Map<String, String> config = new HashMap<>();
//        config.put("cloud_name", "dhxb2civn");
//        config.put("api_key", "791541517185888");
//        config.put("api_secret", "GFV1357EDbPw9tFoCHFtdMWKHQY");
//        MediaManager.init(this, config);
//    }
}