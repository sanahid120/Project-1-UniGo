package unigo.bd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserNoticeBoardAdapter extends RecyclerView.Adapter<UserNoticeBoardAdapter.NoticeViewHolder> {
    private Context context;
    private List<Notice> noticeList;

    public UserNoticeBoardAdapter(Context context, List<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.titleTextView.setText(notice.getTitle());
        holder.titleTextView.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked: " + notice.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, NoticeDetailActivity.class);
            intent.putExtra("title", notice.getTitle());
            intent.putExtra("description", notice.getDescription());
            intent.putExtra("imageUrl", notice.getImageUrl());
            intent.putExtra("Date",notice.getDate());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_NoticeTitle);

        }
    }
}
