package unigo.bd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class NoticeDetailActivity extends AppCompatActivity {
    private TextView titleTextView, descriptionTextView;
    private ImageView noticeImageView;
    private TextView Date;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        titleTextView = findViewById(R.id.tv_NoticeTitle);
        descriptionTextView = findViewById(R.id.tvNoticeDescription);
        noticeImageView = findViewById(R.id.imageView_notice);
        Date = findViewById(R.id.date_noticeBoard);
        back = findViewById(R.id.back_button_noticedetails);
        back.setOnClickListener(v-> finish());

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String date = getIntent().getStringExtra("Date");

        titleTextView.setText(title);
        if (description!=null){
            descriptionTextView.setText(description);
        }
        else {
            descriptionTextView.setVisibility(View.GONE);
        }
        Date.setText(date);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(noticeImageView);
        } else {
            noticeImageView.setVisibility(View.GONE);
        }
    }
}
