package unigo.bd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private Context context;
    private List<Notice> noticeList;
    private DatabaseReference databaseReference;

    public NoticeAdapter(Context context, List<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Notices");
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.titleTextView.setText(notice.getTitle());
        String description= notice.getDescription();
        String imageUrl = notice.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
        if (!description.isEmpty()) {
            holder.DescriptionTextView.setText(description);
        } else {
            holder.DescriptionTextView.setVisibility(View.GONE);
        }

//        // Click listener to open details
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, NoticeDetailActivity.class);
//            intent.putExtra("title", notice.getTitle());
//            intent.putExtra("description", notice.getDescription());
//            intent.putExtra("imageUrl", notice.getImageUrl());
//            context.startActivity(intent);
//        });

        holder.deleteButton.setOnClickListener(v -> {
            String noticeId = notice.getId();
            databaseReference.child(noticeId).removeValue()
                    .addOnSuccessListener(a -> {
                        Toast.makeText(context, "Notice Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView,DescriptionTextView;
        Button deleteButton;
        ImageView image;


        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.et_NoticeTitle);
            deleteButton = itemView.findViewById(R.id.btnDelete);
            DescriptionTextView = itemView.findViewById(R.id.et_noticeContent);
            image = itemView.findViewById(R.id.imageView_notice);

        }
    }
}
