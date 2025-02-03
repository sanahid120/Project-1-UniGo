package unigo.bd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class NoticeDetailActivity extends AppCompatActivity {
    private TextView titleTextView, descriptionTextView;
    private ImageView noticeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        titleTextView = findViewById(R.id.tv_NoticeTitle);
        descriptionTextView = findViewById(R.id.tv_NoticeDescription);
        noticeImageView = findViewById(R.id.imageView_notice);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(noticeImageView);
        } else {
            noticeImageView.setVisibility(View.GONE);
        }
    }
}
