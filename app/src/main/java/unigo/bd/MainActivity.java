package unigo.bd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView appNameText;
    private String appName = "UniGo";
    private int index = 0;
    private long delay = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}